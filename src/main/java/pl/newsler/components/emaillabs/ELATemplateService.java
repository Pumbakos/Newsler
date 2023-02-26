package pl.newsler.components.emaillabs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.components.emaillabs.exception.ELATemplateDeletionException;
import pl.newsler.components.emaillabs.exception.ELAValidationRequestException;
import pl.newsler.components.emaillabs.executor.ELARequestBuilder;
import pl.newsler.components.emaillabs.usecase.ELATemplateAddResponse;
import pl.newsler.components.user.NLUser;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
class ELATemplateService implements IELATemplateService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ELARequestBuilder requestBuilder;
    private final RestTemplate restTemplate;

    @Override
    public String add(final NLUser user, final String html, final String text) throws ELAValidationRequestException {
        final Map<String, String> params = new LinkedHashMap<>(2);

        if (!StringUtils.isBlank(html)) {
            params.put(ELAParam.HTML, html);
        }
        if (!StringUtils.isBlank(text)) {
            params.put(ELAParam.TEXT, text);
        }
        if (params.isEmpty()) {
            throw new InvalidUserDataException("Input", "Fields 'text' and 'html' are not set, are empty");
        }

        final HttpEntity<String> entity = new HttpEntity<>(requestBuilder.buildUrlEncoded(params), requestBuilder.buildAuthHeaders(user));
        final UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(ELARequestPoint.BASE_URL)
                .path(ELARequestPoint.ADD_TEMPLATE_URL)
                .build();
        final ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                final ELATemplateAddResponse template = mapper.readValue(response.getBody(), ELATemplateAddResponse.class);
                return template.getTemplateId();
            }
        } catch (Exception e) {
            throw new ELAValidationRequestException();
        }
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new InvalidUserDataException("AppKey or SecretKey", "Invalid");
        }
        throw new InvalidUserDataException("Templates", "Too many templates. (max 50)");
    }

    @Override
    public void remove(final NLUser user, final String templateId) throws ELATemplateDeletionException {
        final HttpEntity<String> entity = new HttpEntity<>(requestBuilder.buildAuthHeaders(user));
        final UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(ELARequestPoint.BASE_URL)
                .path(ELARequestPoint.DELETE_TEMPLATE_URL.replace("{templateId}", templateId))
                .build();
        final ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(uriComponents.toUri(), HttpMethod.DELETE, entity, String.class);
        } catch (RestClientException e) {
            throw new ELATemplateDeletionException();
        }

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new InvalidUserDataException("AppKey or SecretKey", "Invalid");
        }
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new InvalidUserDataException("Template", "Not found");
        }
    }
}
