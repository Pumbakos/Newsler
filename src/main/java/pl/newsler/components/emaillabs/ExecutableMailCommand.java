package pl.newsler.components.emaillabs;

import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.newsler.components.user.NLUser;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor(staticName = "of")
class ExecutableMailCommand implements Runnable {
    private final NLUser user;
    private final MailDetails details;

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
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String userPass = user.getAppKey() + ":" + user.getSecretKey();
        String auth = "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes(StandardCharsets.UTF_8));

        WebClient client = WebClient.builder()
                .baseUrl("https://api.emaillabs.net.pl/api")
                .defaultHeader(HttpHeaders.AUTHORIZATION, auth)
                .build();

        String userEmail = user.getEmail().getValue();
        params.put(Param.FROM, Collections.singletonList(userEmail));
        String name = String.format("%s %s", user.getFirstName(), user.getLastName());
        params.put(Param.FROM_NAME, Collections.singletonList(name));
        params.put(Param.SMTP_ACCOUNT, Collections.singletonList(user.getSmtpAccount().getValue()));
        String key = String.format(Param.TO_ADDRESS_NAME, createToAddressesArray(details), name);
        params.put(key, Collections.singletonList(""));
        params.put(Param.SUBJECT, Collections.singletonList(details.getSubject()));
        params.put(Param.HTML, Collections.singletonList(String.format("<pre>%s</pre>", details.getMessage())));
        params.put(Param.TEXT, Collections.singletonList(details.getMessage()));

        BodyInserters.FormInserter<String> formData = BodyInserters.fromFormData(params);

        ELASendMailResponse response = client.post()
                .uri("URL")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .bodyToMono(ELASendMailResponse.class)
                .block();

        log.info(String.format("QUERY: %s%n", response));
    }

    private String createToAddressesArray(MailDetails details) {
        StringBuilder builder = new StringBuilder();
        details.getToAddresses().forEach(email -> builder.append(email).append(","));
        int i = builder.length();
        builder.delete(i - 1, i);
        return builder.toString();
    }
}
