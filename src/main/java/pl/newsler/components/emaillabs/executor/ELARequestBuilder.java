package pl.newsler.components.emaillabs.executor;

import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.newsler.components.emaillabs.ELAParam;
import pl.newsler.components.user.NLUser;
import pl.newsler.internal.NewslerDesignerServiceProperties;
import pl.newsler.security.NLIPasswordEncoder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ELARequestBuilder {
    private final NLIPasswordEncoder passwordEncoder;
    @Value("${newsler.designer.schema}")
    private NewslerDesignerServiceProperties.Schema schema;
    @Value("${newsler.designer.domain-name}")
    private String domain;
    @Value("${newsler.designer.port}")
    private int port;

    public <T extends ELAMailDetails> Map<String, String> buildParamsMap(final NLUser user, final T details) {
        final Map<String, String> params = new LinkedHashMap<>();
        final String name = String.format("%s %s", user.getFirstName(), user.getLastName());

        params.put(ELAParam.FROM, user.getEmail().getValue());
        params.put(ELAParam.FROM_NAME, name);

        details.toAddresses.forEach(address -> params.put(String.format(ELAParam.TO, address, ""), ""));

        params.put(ELAParam.SUBJECT, details.subject());
        params.put(ELAParam.HTML, buildMessage(user, details, MessageType.HTML));
        params.put(ELAParam.TEXT, buildMessage(user, details, MessageType.PLAIN));

        return params;
    }

    public String buildUrlEncoded(final Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }

        final StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return builder.toString();
    }

    @NotNull
    public MultiValueMap<String, String> buildAuthHeaders(final NLUser user) {
        final String userPass = passwordEncoder.decrypt(user.getAppKey().getValue()) + ":" + passwordEncoder.decrypt(user.getSecretKey().getValue());
        final String auth = "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes(StandardCharsets.UTF_8));

        final LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.AUTHORIZATION, auth);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return headers;
    }

    private <T extends ELAMailDetails> String buildMessage(NLUser user, T details, MessageType type) {
        final String encodedEmail = URLEncoder.encode(user.getEmail().getValue(), StandardCharsets.UTF_8); //! fixme: receiver mail, not user's one!
        final String cancellationToken = user.getCancellationToken().getValue();
        return details.message.concat(String.format(type.format, schema, domain, port, cancellationToken, encodedEmail));
    }

    private enum MessageType {
        HTML("</br></br><pre><em><a href=\"%s://%s:%d/subscription/cancel?token=%s&email=%s\" style=\"text-decoration: none; font-size: .6rem;\">Unsubscribe from newsletter</a></em></pre>"),
        PLAIN("\n\nUnsubscribe from newsletter: %s://%s:%d/subscription/cancel?token=%s&email=%s");

        final String format;

        MessageType(final String format) {
            this.format = format;
        }
    }
}
