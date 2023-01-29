package pl.newsler.components.emaillabs;

import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLIdType;
import pl.newsler.components.emaillabs.dto.MailSendRequest;

import java.util.List;
import java.util.UUID;

public record MailDetails(NLUuid id, List<String> toAddresses, List<String> cc, List<String> bcc, String subject,
                          String message) {
    public static MailDetails of(MailSendRequest request) {
        return new MailDetails(NLUuid.of(UUID.randomUUID(), NLIdType.MAIL), request.toAddresses(), request.cc(), request.bcc(), request.subject(), request.message());
    }
}
