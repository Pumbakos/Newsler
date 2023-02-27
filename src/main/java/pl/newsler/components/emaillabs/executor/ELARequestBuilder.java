package pl.newsler.components.emaillabs.executor;

import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.newsler.components.emaillabs.ELAParam;
import pl.newsler.components.emaillabs.ELATemplateParam;
import pl.newsler.components.emaillabs.exception.ELAParameterBuildException;
import pl.newsler.components.htmlremover.HtmlTagRemover;
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
    private String domainName;
    @Value("${newsler.designer.port}")
    private int port;

    public <T extends ELAMailDetails> Map<String, String> buildParamsMap(final NLUser user, final T details) {
        final Map<String, String> params = new LinkedHashMap<>();
        final String name = String.format("%s %s", user.getFirstName(), user.getLastName());

        params.put(ELAParam.FROM, user.getEmail().getValue());
        params.put(ELAParam.FROM_NAME, name);

        final String templateId = user.getDefaultTemplateId().getValue();
        if (StringUtils.isBlank(templateId)) {
            throw new ELAParameterBuildException("TemplateID", "Null or empty");
        }

        final String stripedMessage = HtmlTagRemover.remove(details.message);
        params.put(ELATemplateParam.TEMPLATE_ID, templateId);
        params.put(ELAParam.SUBJECT, details.subject());
        params.put(ELAParam.HTML, details.message);
        params.put(ELAParam.TEXT, stripedMessage);
        buildToAddresses(user, details, params, stripedMessage);

        return params;
    }

    private <T extends ELAMailDetails> void buildToAddresses(final NLUser user, final T details,
                                                             final Map<String, String> params, final String textMessage) {
        final String cancellationToken = user.getCancellationToken().getValue();
        details.toAddresses.forEach(address -> {
                    params.put(String.format(ELATemplateParam.TO_WITH_VARS, address, "messageHtml"), details.message);
                    params.put(String.format(ELATemplateParam.TO_WITH_VARS, address, "messageText"), textMessage);
                    params.put(String.format(ELATemplateParam.TO_WITH_VARS, address, "cancellationSection"), String.format("<p><a href=\"%s://%s:%d/subscription/cancel?token=%s&email=%s\">Unsubscribe from newsletter</a></p>", schema.getName(), domainName, port, cancellationToken, address));
                }
        );
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
}
