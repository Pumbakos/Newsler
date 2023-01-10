package pl.newsler.components.emaillabs;

import com.google.gson.Gson;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.newsler.components.emaillabs.exceptions.ELASendMailException;
import pl.newsler.components.user.NLUser;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor(staticName = "of")
class ExecutableMailCommand implements Runnable {
    private static final String BASE_URL = "https://api.emaillabs.net.pl/api";
    private static final String SEND_MAIL_URL = "/new_sendmail";
    private final Gson gson = new Gson();
    private final MailDetails details;
    private final NLUser user;

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        final Map<String, String> params = new LinkedHashMap<>();
        String userPass = user.getAppKey() + ":" + user.getSecretKey();
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

        final RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, entity, String.class);
            log.info(String.format("QUERY: %s%n", gson.toJson(response)));
        } catch (RestClientException e) {
            throw new ELASendMailException("Send mail unsuccessful", "Check data, it is likely that SMTP, APP KEY or SECRET KEY are incorrect.");
        }
    }

    private String createToAddressesArray(MailDetails details) {
        StringBuilder builder = new StringBuilder();
        details.toAddresses().forEach(email -> builder.append(email).append(","));
        int i = builder.length();
        builder.delete(i - 1, i);
        return builder.toString();
    }
}
