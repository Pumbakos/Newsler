package pl.newsler.components.emaillabs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
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
import pl.newsler.components.emaillabs.dto.ELASendMailResponse;
import pl.newsler.components.emaillabs.dto.ELASentMailResults;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.security.NLIPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

@Slf4j
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ELATaskExecutor extends ConcurrentTaskExecutor {
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

    void queue(NLId userId, MailDetails details) {
        mailRepository.save(NLUserMail.of(userId, details));
        queue.add(Pair.of(userId, details));
        if (!queueExecution) {
            queueExecution = true;
            executeQueue();
        }
    }

    private void executeQueue() {
        try {
            super.execute(this::execute);
            log.info("Scheduled another task.");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void execute() {
        Pair<NLId, MailDetails> pair = queue.poll();
        if (pair == null) {
            queueExecution = false;
            return;
        }

        getAndExecute(pair);
        execute();
    }

    private void getAndExecute(Pair<NLId, MailDetails> pair) {
        final Optional<NLUser> optionalUser = userRepository.findById(pair.getLeft());
        optionalUser.ifPresentOrElse(
                nlUser -> execution(pair.getRight(), nlUser),
                () -> log.debug("No more mail jobs, waiting...")
        );
    }

    private void execution(MailDetails details, NLUser user) {
        final ELASentMailResults results = call(user, details);
        final Optional<NLUserMail> optionalUserMail = mailRepository.findById(results.getId());
        optionalUserMail.ifPresent(m -> {
            m.setStatus(results.getStatus());
            m.setErrorMessage(NLStringValue.of(results.getMessage()));
            mailRepository.save(m);
        });
    }

    private ELASentMailResults call(NLUser user, MailDetails details) {
        log.info("Executing task {}", details.id());
        final Map<String, String> params = new LinkedHashMap<>();
        String userPass = passwordEncoder.decrypt(user.getAppKey().getValue()) + ":" + passwordEncoder.decrypt(user.getSecretKey().getValue());
        String auth = "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes(StandardCharsets.UTF_8));

        String name = String.format("%s %s", user.getFirstName(), user.getLastName());
        params.put(Param.FROM, user.getEmail().getValue());
        params.put(Param.FROM_NAME, name);
        params.put(Param.SMTP_ACCOUNT, passwordEncoder.decrypt(user.getSmtpAccount().getValue()));
        params.put(String.format(Param.TO_ADDRESS_NAME, user.getEmail().getValue(), name), Arrays.toString(details.toAddresses().toArray()));
        params.put(Param.SUBJECT, details.subject());
        params.put(Param.HTML, String.format("<pre>%s</pre>", details.message()));
        params.put(Param.TEXT, details.message());

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

    private ELASentMailResults handleResponse(ResponseEntity<String> response, MailDetails details, NLId userId) {
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
}
