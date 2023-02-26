package pl.newsler.components.emaillabs;

import pl.newsler.components.emaillabs.exception.ELATemplateDeletionException;
import pl.newsler.components.emaillabs.exception.ELAValidationRequestException;
import pl.newsler.components.user.NLUser;

public interface IELATemplateService {
    String DEFAULT_HTML_FOOTER = "</br></br><pre><em><a href=\"%s://%s:%d/subscription/cancel?token=%s&email=%s\" style=\"text-decoration: none; font-size: .6rem;\">Unsubscribe from newsletter</a></em></pre>";
    String DEFAULT_TEXT_FOOTER = "\n\nUnsubscribe from newsletter: %s://%s:%d/subscription/cancel?token=%s&email=%s";
    String add(NLUser user, String html, String text) throws ELAValidationRequestException;

    void remove(NLUser user, String templateId) throws ELATemplateDeletionException;
}
