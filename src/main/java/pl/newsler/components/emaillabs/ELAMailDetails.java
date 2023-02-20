package pl.newsler.components.emaillabs;

import pl.newsler.commons.model.NLUuid;
import pl.newsler.commons.model.NLIdType;
import pl.newsler.components.emaillabs.usecase.ELAMailSendRequest;

import java.util.List;
import java.util.UUID;

public record ELAMailDetails(NLUuid id, List<String> toAddresses, List<String> cc, List<String> bcc, String subject,
                             String message) {
    public static ELAMailDetails of(ELAMailSendRequest request) {
        return new ELAMailDetails(NLUuid.of(UUID.randomUUID(), NLIdType.MAIL), request.to(), request.cc(), request.bcc(), request.subject(), request.message());
    }
}
