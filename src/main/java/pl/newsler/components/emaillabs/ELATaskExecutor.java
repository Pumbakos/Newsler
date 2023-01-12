package pl.newsler.components.emaillabs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.google.gson.Gson;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.newsler.commons.models.NLEmailStatus;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLStringValue;
import pl.newsler.components.emaillabs.exceptions.ELASendMailResponse;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.security.NLIPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@RequiredArgsConstructor
public class ELATaskExecutor extends ConcurrentTaskScheduler {
    private static final String BASE_URL = "https://api.emaillabs.net.pl/api";
    private static final String SEND_MAIL_URL = "/new_sendmail";
    private final Queue<Pair<NLId, MailDetails>> queue;
    private final NLIPasswordEncoder passwordEncoder;
    private final IMailRepository mailRepository;
    private final IUserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Gson gson;
    private boolean queueExecution = false;
    private ScheduledFuture<?> activeTask;

    void queue(NLId userId, MailDetails details) {
        mailRepository.save(NLUserMail.of(userId, details));
        queue.add(Pair.of(userId, details));
        if (!queueExecution) {
            queueExecution = true;
            schedule();
        }
    }

    private void schedule() {
        execute();
//        try {
//            activeTask = super.scheduleWithFixedDelay(this::execute, Instant.now(), Duration.ZERO);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    //* FLOW: schedule task #execute at fixed rate -> if there is a dozen of mails (which means submitting did not stop) do not stop executor,
    //* else wait for another invocation of scheduler and check if there are mails to send, send them if so, do nothing otherwise
    private void execute() {
        Pair<NLId, MailDetails> pair = queue.poll();
        if (pair == null) {
            queueExecution = false;
            retrieveActiveTask();
            return;
        }

        getAndExecute(pair);
        execute();
    }

    private void getAndExecute(Pair<NLId, MailDetails> pair) {
        Optional<NLUser> optionalUser = userRepository.findById(pair.getLeft());
        optionalUser.ifPresentOrElse(
                nlUser -> execution(pair.getRight(), nlUser),
                () -> log.debug("No more mail jobs, waiting...")
        );
    }

    private void execution(MailDetails details, NLUser user) {
        ELASentMailResults results = call(user, details);
        Optional<NLUserMail> optionalUserMail = mailRepository.findById(results.getId());
        optionalUserMail.ifPresent(m -> {
            m.setStatus(results.getStatus());
            m.setErrorMessage(NLStringValue.of(results.getMessage()));
            mailRepository.save(m);
        });
    }

    private ELASentMailResults call(NLUser user, MailDetails details) {
        final Map<String, String> params = new LinkedHashMap<>();
        String userPass = passwordEncoder.decrypt(user.getAppKey().getValue()) + ":" + passwordEncoder.decrypt(user.getSecretKey().getValue());
        String auth = "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes(StandardCharsets.UTF_8));

        String name = String.format("%s %s", user.getFirstName(), user.getLastName());
        params.put(Param.FROM, user.getEmail().getValue());
        params.put(Param.FROM_NAME, name);
        params.put(Param.SMTP_ACCOUNT, user.getSmtpAccount().getValue());
        params.put(String.format(Param.TO_ADDRESS_NAME, createToAddressesArray(details), name), "");
        params.put(Param.SUBJECT, details.subject());
        params.put(Param.HTML, String.format("<pre>%s</pre>", details.message()));
        params.put(Param.TEXT, details.message());

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.AUTHORIZATION, auth);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        HttpEntity<String> entity = new HttpEntity<>(ELAUrlParamBuilder.build(params), headers);
        log.info(String.format("Entity: %s%n", gson.toJson(entity)));

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path(SEND_MAIL_URL)
                .build();

        try {
            ResponseEntity<ELASendMailResponse> response = restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, entity, ELASendMailResponse.class);
            log.info(String.format("QUERY: %s%n", gson.toJson(response)));
            return ELASentMailResults.of(details.id(), user.map().getId(), NLEmailStatus.SENT, "Mail sent successfully", LocalDateTime.now());
        } catch (RestClientException e) {
            return handleException(details.id(), user.map().getId(), e);
        }
    }

    @SneakyThrows
    private ELASentMailResults handleException(NLId id, NLId userId, RestClientException e) {
        ELASendMailResponse response = objectMapper.readValue(e.getMessage().substring(e.getMessage().indexOf("{") - 1).substring(1), ELASendMailResponse.class);
        if (e instanceof HttpClientErrorException) {
            return ELASentMailResults.of(id, userId, NLEmailStatus.ERROR, response.getMessage(), LocalDateTime.now());
        }
        if (e instanceof HttpServerErrorException) {
            return ELASentMailResults.of(id, userId, NLEmailStatus.ERROR, response.getMessage(), LocalDateTime.now());
        }

        return ELASentMailResults.of(id, userId, NLEmailStatus.ERROR, "General error. Check data, it is likely that SMTP, APP KEY or SECRET KEY are incorrect.", LocalDateTime.now());
    }

    private String createToAddressesArray(MailDetails details) {
        StringBuilder builder = new StringBuilder();
        details.toAddresses().forEach(email -> builder.append(email).append(","));
        int i = builder.length();
        builder.delete(i - 1, i);
        return builder.toString();
    }

    private void retrieveActiveTask() {
        try {
            if (activeTask != null) {
                activeTask.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Scheduler get() error. {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
