package pl.newsler.components.emaillabs.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.newsler.commons.model.NLEmailStatus;
import pl.newsler.commons.model.NLStringValue;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.ELAParam;
import pl.newsler.components.emaillabs.ELARequestPoint;
import pl.newsler.components.emaillabs.ELAUserMail;
import pl.newsler.components.emaillabs.IELAMailRepository;
import pl.newsler.components.emaillabs.usecase.ELASentMailResults;
import pl.newsler.components.receiver.IReceiverService;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.internal.exception.ConfigurationException;
import pl.newsler.security.NLIPasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

@Slf4j
public abstract class ELAConcurrentTaskExecutor<T extends ELAMailDetails> {
    protected final Queue<Pair<NLUuid, T>> queue;
    protected final IELAMailRepository mailRepository;
    private final ConcurrentTaskExecutor taskExecutor;
    private final NLIPasswordEncoder passwordEncoder;
    private final IReceiverService receiverService;
    private final IUserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ELARequestBuilder requestBuilder;

    @SuppressWarnings("java:S107")
    protected ELAConcurrentTaskExecutor(final Queue<Pair<NLUuid, T>> instantQueue, final ConcurrentTaskExecutor taskExecutor,
                                        final NLIPasswordEncoder passwordEncoder, final IELAMailRepository mailRepository,
                                        final IReceiverService receiverService, final IUserRepository userRepository,
                                        final RestTemplate restTemplate, final ELARequestBuilder requestBuilder) {
        this.queue = instantQueue;
        this.taskExecutor = taskExecutor;
        this.passwordEncoder = passwordEncoder;
        this.mailRepository = mailRepository;
        this.receiverService = receiverService;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
        this.requestBuilder = requestBuilder;
    }

    protected final void execute(final Runnable runnable) {
        taskExecutor.execute(runnable);
    }

    protected final void scheduleAtFixedRate(final Runnable runnable, final Duration duration) {
        if (taskExecutor instanceof ConcurrentTaskScheduler scheduler) {
            try {
                final ZonedDateTime startTime = TimeResolver.getStartTime(ZonedDateTime.now());
                scheduler.scheduleAtFixedRate(runnable, startTime.toInstant(), duration);
                log.info("Scheduled mails queue will be executed first at {} with {} min interval", startTime, duration.toMinutes());
            } catch (Exception e) {
                throw new ConfigurationException("Scheduled mails queue [ConcurrentTaskScheduler] could not be started, no scheduled mails will be sent.");
            }
        } else {
            throw new ConfigurationException("Scheduled mails queue [ConcurrentTaskScheduler] could not be started, no scheduled mails will be sent.");
        }
    }

    protected final ELASentMailResults call(final NLUser user, final T details) {
        log.info("Executing task {}", details.id());
        final MultiValueMap<String, String> headers = requestBuilder.buildAuthHeaders(user);

        final Map<String, String> params = requestBuilder.buildParamsMap(user, details);
        params.put(ELAParam.SMTP_ACCOUNT, passwordEncoder.decrypt(user.getSmtpAccount().getValue()));

        final HttpEntity<String> entity = new HttpEntity<>(requestBuilder.buildUrlEncoded(params), headers);
        final UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(ELARequestPoint.BASE_URL)
                .path(ELARequestPoint.SEND_MAIL_WITH_TEMPLATE_URL)
                .build();

        try {
            final ResponseEntity<String> response = restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, entity, String.class);
            log.info("MAIL {} SENT", details.id());
            return handleResponse(response, details, user.map().getUuid());
        } catch (RestClientException e) {
            log.info("MAIL {} ERROR", details.id());
            return handleException(details.id(), user.map().getUuid(), e);
        }
    }

    protected final ELASentMailResults handleResponse(final ResponseEntity<String> response, final T details, final NLUuid userId) {
        if (response == null) {
            return ELASentMailResults.of(details.id(), userId, NLEmailStatus.ERROR, "EmailLabs server did not respond", LocalDateTime.now());
        }
        if (response.getStatusCode().is2xxSuccessful()) {
            return ELASentMailResults.of(details.id(), userId, NLEmailStatus.SENT, "Mail sent successfully", LocalDateTime.now());
        }
        if (response.getStatusCode().is4xxClientError()) {
            return ELASentMailResults.of(details.id(), userId, NLEmailStatus.ERROR, response.getBody(), LocalDateTime.now());
        }
        if (response.getStatusCode().is5xxServerError()) {
            return ELASentMailResults.of(details.id(), userId, NLEmailStatus.ERROR, response.getBody(), LocalDateTime.now());
        }

        return ELASentMailResults.of(details.id(), userId, NLEmailStatus.ERROR, "General error. Check data, it is likely that SMTP, APP KEY or SECRET KEY are incorrect.", LocalDateTime.now());
    }

    @SneakyThrows(JsonProcessingException.class)
    protected final ELASentMailResults handleException(final NLUuid id, final NLUuid userId, final RestClientException e) {
        final String response = objectMapper.readValue(e.getMessage(), String.class);
        log.info(response);
        if (e instanceof HttpClientErrorException) {
            return ELASentMailResults.of(id, userId, NLEmailStatus.ERROR, e.getMessage(), LocalDateTime.now());
        }
        if (e instanceof HttpServerErrorException) {
            return ELASentMailResults.of(id, userId, NLEmailStatus.ERROR, e.getMessage(), LocalDateTime.now());
        }

        return ELASentMailResults.of(id, userId, NLEmailStatus.ERROR, "General error. Check data, it is likely that SMTP, APP KEY or SECRET KEY are incorrect.", LocalDateTime.now());
    }

    protected final void addReceiverIfNotExist(final T details, final NLUuid uuid) {
        receiverService.autoSaveNewReceiver(details.toAddresses, uuid);
    }

    protected final void getUserAndExecute(final Pair<NLUuid, T> pair) {
        final Optional<NLUser> optionalUser = userRepository.findById(pair.getLeft());
        optionalUser.ifPresent(user -> execution(pair.getRight(), user));
    }

    protected void execution(final T details, final NLUser user) {
        final NLUuid uuid = user.map().getUuid();
        addReceiverIfNotExist(details, uuid);
        final ELASentMailResults results = call(user, details);
        final Optional<ELAUserMail> optionalUserMail = mailRepository.findById(results.getId());
        optionalUserMail.ifPresent(m -> {
            m.setStatus(results.getStatus());
            m.setErrorMessage(NLStringValue.of(results.getMessage()));
            mailRepository.save(m);
        });
    }
}
