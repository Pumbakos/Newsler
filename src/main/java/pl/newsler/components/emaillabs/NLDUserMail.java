package pl.newsler.components.emaillabs;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import pl.newsler.commons.models.NLEmailMessage;
import pl.newsler.commons.models.NLEmailStatus;
import pl.newsler.commons.models.NLStringValue;
import pl.newsler.commons.models.NLSubject;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.components.emaillabs.dto.GetMailResponse;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NLDUserMail {
    NLUuid id;
    NLStringValue toAddresses;
    NLStringValue cc;
    NLStringValue bcc;
    NLSubject subject;
    NLEmailMessage message;
    NLEmailStatus status;
    NLStringValue errorMessage;

    public static NLDUserMail of(NLUserMail mail) {
        return NLDUserMail.builder()
                .id(mail.getId())
                .toAddresses(mail.getToAddresses())
                .cc(mail.getCc())
                .bcc(mail.getBcc())
                .subject(mail.getSubject())
                .message(mail.getMessage())
                .status(mail.getStatus())
                .errorMessage(mail.getErrorMessage())
                .build();
    }
}
