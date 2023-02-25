package pl.newsler.components.emaillabs.executor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.newsler.components.emaillabs.ELAParam;
import pl.newsler.components.htmlremover.HtmlTagRemover;
import pl.newsler.components.user.NLUser;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ELAParamBuilder {
    public static <T extends ELAMailDetails> Map<String, String> buildParamsMap(final NLUser user, final T details) {
        final Map<String, String> params = new LinkedHashMap<>();

        final String name = String.format("%s %s", user.getFirstName(), user.getLastName());
        params.put(ELAParam.FROM, user.getEmail().getValue());
        params.put(ELAParam.FROM_NAME, name);

        details.toAddresses.forEach(address -> params.put(String.format(ELAParam.TO, address, ""), ""));

        params.put(ELAParam.SUBJECT, details.subject());
        params.put(ELAParam.HTML, details.message());
        params.put(ELAParam.TEXT, HtmlTagRemover.remove(details.message()));

        return params;
    }

    public static String buildUrlEncoded(final Map<String, String> map) {
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
}
