package pl.newsler.components.emaillabs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.newsler.components.emaillabs.dto.MailSendRequest;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class MailDetails {
    private final Map<String, String> toAddresses;
    private final List<String> cc;
    private final List<String> bcc;
    private final String subject;
    private final String message;

    public static MailDetails of(MailSendRequest request) {
        return new MailDetails(request.toAddresses(), request.cc(), request.bcc(), request.subject(), request.message());
    }
}
