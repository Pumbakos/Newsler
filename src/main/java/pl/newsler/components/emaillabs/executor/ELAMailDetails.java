package pl.newsler.components.emaillabs.executor;

import pl.newsler.commons.model.NLUuid;

import java.util.List;

abstract class ELAMailDetails {
    protected final NLUuid id;
    protected final List<String> toAddresses;
    protected final String subject;
    protected final String message;

    protected ELAMailDetails(NLUuid id, List<String> toAddresses, String subject,
                             String message) {
        this.id = id;
        this.toAddresses = toAddresses;
        this.subject = subject;
        this.message = message;
    }

    NLUuid id() {
        return id;
    }

    List<String> toAddresses() {
        return toAddresses;
    }

    String subject() {
        return subject;
    }

    String message() {
        return message;
    }
}
