package pl.newsler.components.emaillabs.executor;

import pl.newsler.commons.model.NLIdType;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.usecase.ELAInstantMailRequest;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ELAInstantMailDetails extends ELAMailDetails {
    private ELAInstantMailDetails(final NLUuid id, final List<String> toAddresses, final String subject, final String message) {
        super(id, toAddresses, subject, message);
    }

    public static ELAInstantMailDetails of(ELAInstantMailRequest request) {
        return new ELAInstantMailDetails(NLUuid.of(UUID.randomUUID(), NLIdType.MAIL), request.to(), request.subject(), request.message());
    }

    @Override
    public NLUuid id() {
        return super.id;
    }

    @Override
    public List<String> toAddresses() {
        return super.toAddresses;
    }

    @Override
    public String subject() {
        return super.subject;
    }

    @Override
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
                Objects.equals(super.subject, that.subject) &&
                Objects.equals(super.message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.id, super.toAddresses, super.subject, super.message);
    }

    @Override
    public String toString() {
        return "ELAInstantMailDetails[" +
                "id=" + super.id + ", " +
                "toAddresses=" + super.toAddresses + ", " +
                "subject=" + super.subject + ", " +
                "message=" + super.message + ']';
    }
}
