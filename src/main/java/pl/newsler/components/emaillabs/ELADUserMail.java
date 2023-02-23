package pl.newsler.components.emaillabs;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import pl.newsler.commons.model.NLEmailMessage;
import pl.newsler.commons.model.NLEmailStatus;
import pl.newsler.commons.model.NLStringValue;
import pl.newsler.commons.model.NLSubject;
import pl.newsler.commons.model.NLUuid;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ELADUserMail {
    NLUuid id;
    NLStringValue toAddresses;
    NLStringValue cc;
    NLStringValue bcc;
    NLSubject subject;
    NLEmailMessage message;
    NLEmailStatus status;
    NLStringValue errorMessage;

    public static ELADUserMail of(ELAUserMail mail) {
        return ELADUserMail.builder()
                .id(mail.getId())
                .toAddresses(mail.getToAddresses())
                .subject(mail.getSubject())
                .message(mail.getMessage())
                .status(mail.getStatus())
                .errorMessage(mail.getErrorMessage())
                .build();
    }
}
