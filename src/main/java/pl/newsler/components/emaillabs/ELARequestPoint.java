package pl.newsler.components.emaillabs;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ELARequestPoint {
    public static final String BASE_URL = "https://api.emaillabs.net.pl/api";
    public static final String SEND_MAIL_URL = "/new_sendmail";
    public static final String SEND_MAIL_WITH_TEMPLATE_URL = "/sendmail_templates";
    public static final String ADD_TEMPLATE_URL = "/add_template";
    public static final String DELETE_TEMPLATE_URL = "/delete_temlate/{templateId}";
}
