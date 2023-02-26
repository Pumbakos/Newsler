package pl.newsler.components.emaillabs.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import pl.newsler.components.emaillabs.ELAParam;
import pl.newsler.components.user.NLUser;
import pl.newsler.internal.NewslerDesignerServiceProperties;
import pl.newsler.internal.NewslerServiceProperties;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ELAParamBuilder {
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

    private <T extends ELAMailDetails> String buildMessage(NLUser user, T details, MessageType type) {
        final String encodedEmail = URLEncoder.encode(user.getEmail().getValue(), StandardCharsets.UTF_8);
        final String cancellationToken = user.getCancellationToken().getValue();
        return details.message.concat(String.format(type.format, schema, domain, port, cancellationToken, encodedEmail));
    }

    private enum MessageType {
        HTML("</br></br><pre><em><a href=\"%s://%s:%d/subscription/cancel?token=%s&email=%s\">Unsubscribe from newsletter</a></em></pre>"),
        PLAIN("\n\nUnsubscribe from newsletter: %s://%s:%d/subscription/cancel?token=%s&email=%s");

        final String format;

        MessageType(final String format) {
            this.format = format;
        }
    }
}
