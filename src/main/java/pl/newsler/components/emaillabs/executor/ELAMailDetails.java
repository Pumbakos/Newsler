package pl.newsler.components.emaillabs.executor;

import pl.newsler.commons.model.NLUuid;

import java.util.List;

abstract class ELAMailDetails {
    protected final NLUuid id;
    protected final List<String> toAddresses;
    protected final List<String> cc;
    protected final List<String> bcc;
    protected final String subject;
    protected final String message;

    protected ELAMailDetails(NLUuid id, List<String> toAddresses, List<String> cc, List<String> bcc, String subject,
                             String message) {
        this.id = id;
        this.toAddresses = toAddresses;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.message = message;
    }

    NLUuid id() {
        return id;
    }

    List<String> toAddresses() {
        return toAddresses;
    }

    List<String> cc() {
        return cc;
    }

    List<String> bcc() {
        return bcc;
    }

    String subject() {
        return subject;
    }

    String message() {
        return message;
    }
}
