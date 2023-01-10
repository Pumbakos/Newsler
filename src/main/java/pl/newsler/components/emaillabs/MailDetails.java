package pl.newsler.components.emaillabs;

import pl.newsler.components.emaillabs.dto.MailSendRequest;

import java.util.List;

public record MailDetails(List<String> toAddresses, List<String> cc, List<String> bcc, String subject, String message) {
    public static MailDetails of(MailSendRequest request) {
        return new MailDetails(request.toAddresses(), request.cc(), request.bcc(), request.subject(), request.message());
    }
}
