package pl.newsler.components.emaillabs.executor;

import pl.newsler.commons.model.NLIdType;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.usecase.ELAMailSendRequest;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ELAInstantMailDetails extends ELAMailDetails {
    private ELAInstantMailDetails(final NLUuid id, final List<String> toAddresses, final List<String> cc, final List<String> bcc, final String subject, final String message) {
        super(id, toAddresses, cc, bcc, subject, message);
    }

    public static ELAInstantMailDetails of(ELAMailSendRequest request) {
        return new ELAInstantMailDetails(NLUuid.of(UUID.randomUUID(), NLIdType.MAIL), request.to(), request.cc(), request.bcc(), request.subject(), request.message());
    }

    public NLUuid id() {
        return super.id;
    }

    public List<String> toAddresses() {
        return super.toAddresses;
    }

    public List<String> cc() {
        return super.cc;
    }

    public List<String> bcc() {
        return super.bcc;
    }

    public String subject() {
        return super.subject;
    }

    public String message() {
        return super.message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ELAInstantMailDetails) obj;
        return Objects.equals(super.id, that.id) &&
                Objects.equals(super.toAddresses, that.toAddresses) &&
                Objects.equals(super.cc, that.cc) &&
                Objects.equals(super.bcc, that.bcc) &&
                Objects.equals(super.subject, that.subject) &&
                Objects.equals(super.message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.id, super.toAddresses, super.cc, super.bcc, super.subject, super.message);
    }

    @Override
    public String toString() {
        return "ELAInstantMailDetails[" +
                "id=" + super.id + ", " +
                "toAddresses=" + super.toAddresses + ", " +
                "cc=" + super.cc + ", " +
                "bcc=" + super.bcc + ", " +
                "subject=" + super.subject + ", " +
                "message=" + super.message + ']';
    }
}
