package pl.newsler.components.emaillabs.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.util.LinkedMultiValueMap;
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
import pl.newsler.components.emaillabs.ELAUserMail;
import pl.newsler.components.emaillabs.IELAMailRepository;
import pl.newsler.components.emaillabs.usecase.ELASendMailResponse;
import pl.newsler.components.emaillabs.usecase.ELASentMailResults;
import pl.newsler.components.htmlremover.HtmlTagRemover;
import pl.newsler.components.receiver.IReceiverService;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.internal.exception.ConfigurationException;
import pl.newsler.security.NLIPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

@Slf4j
public abstract class ELAConcurrentTaskExecutor<T extends ELAMailDetails> {
    protected static final String BASE_URL = "https://api.emaillabs.net.pl/api";
    protected static final String SEND_MAIL_URL = "/new_sendmail";
    protected final Queue<Pair<NLUuid, T>> queue;
    protected final ConcurrentTaskExecutor taskExecutor;
    protected final NLIPasswordEncoder passwordEncoder;
    protected final IELAMailRepository mailRepository;
    protected final IReceiverService receiverService;
    protected final IUserRepository userRepository;
    protected final RestTemplate restTemplate;
    protected final ObjectMapper objectMapper;

    @SuppressWarnings("java:S107")
    protected ELAConcurrentTaskExecutor(final Queue<Pair<NLUuid, T>> instantQueue, final ConcurrentTaskExecutor taskExecutor,
                                        final NLIPasswordEncoder passwordEncoder, final IELAMailRepository mailRepository,
                                        final IReceiverService receiverService, final IUserRepository userRepository,
                                        final RestTemplate restTemplate, final ObjectMapper objectMapper) {
        this.queue = instantQueue;
        this.taskExecutor = taskExecutor;
        this.passwordEncoder = passwordEncoder;
        this.mailRepository = mailRepository;
        this.receiverService = receiverService;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    protected final void execute(final Runnable runnable) {
        taskExecutor.execute(runnable);
    }

    protected final void scheduleAtFixedRate(Runnable runnable, Duration duration) {
        if (taskExecutor instanceof ConcurrentTaskScheduler scheduler) {
            try {
                Instant instant = TimeResolver.getStartTime().toInstant();
                scheduler.scheduleAtFixedRate(runnable, instant, duration);
                log.info("Scheduled mails queue will be executed first at {} with {} min interval", instant, duration.toMinutes());
            } catch (Exception e) {
                throw new ConfigurationException("Scheduled mails queue [ConcurrentTaskScheduler] could not be started, no scheduled mails will be sent.");
            }
        } else {
            throw new ConfigurationException("Scheduled mails queue [ConcurrentTaskScheduler] could not be started, no scheduled mails will be sent.");
        }
    }

    protected final ELASentMailResults call(NLUser user, T details) {
        log.info("Executing task {}", details.id());
        final Map<String, String> params = new LinkedHashMap<>();
        String userPass = passwordEncoder.decrypt(user.getAppKey().getValue()) + ":" + passwordEncoder.decrypt(user.getSecretKey().getValue());
        String auth = "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes(StandardCharsets.UTF_8));

        String name = String.format("%s %s", user.getFirstName(), user.getLastName());
        params.put(ELAParam.FROM, user.getEmail().getValue());
        params.put(ELAParam.FROM_NAME, name);
        params.put(ELAParam.SMTP_ACCOUNT, passwordEncoder.decrypt(user.getSmtpAccount().getValue()));
        params.put(String.format(ELAParam.TO_ADDRESS_NAME, user.getEmail().getValue(), name), Arrays.toString(details.toAddresses().toArray()));
        params.put(ELAParam.SUBJECT, details.subject());
        params.put(ELAParam.HTML, details.message());
        params.put(ELAParam.TEXT, HtmlTagRemover.remove(details.message()));

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.AUTHORIZATION, auth);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        HttpEntity<String> entity = new HttpEntity<>(ELAUrlParamBuilder.build(params), headers);

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path(SEND_MAIL_URL)
                .build();

        try {
            ResponseEntity<String> response = restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, entity, String.class);
            log.info("MAIL {} SENT", details.id());
            return handleResponse(response, details, user.map().getId());
        } catch (RestClientException e) {
            log.info("MAIL {} ERROR", details.id());
            return handleException(details.id(), user.map().getId(), e);
        }
    }

    protected final ELASentMailResults handleResponse(ResponseEntity<String> response, T details, NLUuid userId) {
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
    protected final ELASentMailResults handleException(NLUuid id, NLUuid userId, RestClientException e) {
        final ELASendMailResponse response = objectMapper.readValue(e.getMessage().substring(e.getMessage().indexOf("{") - 1).substring(1), ELASendMailResponse.class);
        log.info(this.objectMapper.convertValue(e, String.class));
        if (e instanceof HttpClientErrorException) {
            return ELASentMailResults.of(id, userId, NLEmailStatus.ERROR, response.getMessage(), LocalDateTime.now());
        }
        if (e instanceof HttpServerErrorException) {
            return ELASentMailResults.of(id, userId, NLEmailStatus.ERROR, response.getMessage(), LocalDateTime.now());
        }

        return ELASentMailResults.of(id, userId, NLEmailStatus.ERROR, "General error. Check data, it is likely that SMTP, APP KEY or SECRET KEY are incorrect.", LocalDateTime.now());
    }

    protected final void addReceiverIfNotExist(T details, NLUuid uuid) {
        receiverService.autoSaveNewReceiver(details.toAddresses, uuid);
        receiverService.autoSaveNewReceiver(details.cc, uuid);
        receiverService.autoSaveNewReceiver(details.bcc, uuid);
    }

    protected final void getUserAndExecute(Pair<NLUuid, T> pair) {
        final Optional<NLUser> optionalUser = userRepository.findById(pair.getLeft());
        optionalUser.ifPresentOrElse(
                nlUser -> execution(pair.getRight(), nlUser),
                () -> log.debug("No more mail jobs, waiting...")
        );
    }

    public final void execution(T details, NLUser user) {
        final NLUuid uuid = user.map().getId();
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