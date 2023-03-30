package pl.newsler.components.emaillabs;

import pl.newsler.components.emaillabs.exception.ELATemplateDeletionException;
import pl.newsler.components.emaillabs.exception.ELAValidationRequestException;
import pl.newsler.components.user.NLUser;

public interface IELATemplateService {
    String DEFAULT_HTML_TEMPLATE = """
            {{message}}
                        
                        
            {{cancellationSection}}
            """;
    String DEFAULT_TEXT_TEMPLATE = """
            {{message}}
                        
                        
                        
            Unsubscribe from newsletter: {{cancellationLink}}
            """;

    String add(NLUser user, String html, String text) throws ELAValidationRequestException;

    void remove(NLUser user, String templateId) throws ELATemplateDeletionException;
}
