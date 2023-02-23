package pl.newsler.components.emaillabs.executor;

import pl.newsler.commons.model.NLIdType;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.usecase.ELAMailScheduleRequest;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ELAScheduleMailDetails extends ELAMailDetails {
    private final ZonedDateTime zonedDateTime;
    private final String zoneId;

    @SuppressWarnings({"java:S107"})
    private ELAScheduleMailDetails(final NLUuid id, final List<String> toAddresses, final String subject, final String message, final ZonedDateTime zonedDateTime, final String zoneId) {
        super(id, toAddresses, subject, message);
        this.zonedDateTime = zonedDateTime;
        this.zoneId = zoneId;
    }

    public static ELAScheduleMailDetails of(ELAMailScheduleRequest request, ZonedDateTime scheduleTime) {
        return new ELAScheduleMailDetails(NLUuid.of(UUID.randomUUID(), NLIdType.MAIL), request.to(), request.subject(), request.message(), scheduleTime, request.zone());
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

    public ZonedDateTime zonedDateTime() {
        return zonedDateTime;
    }

    public String zoneId() {
        return zoneId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ELAScheduleMailDetails) obj;
        return Objects.equals(super.id, that.id) &&
                Objects.equals(super.toAddresses, that.toAddresses) &&
                Objects.equals(super.subject, that.subject) &&
                Objects.equals(super.message, that.message) &&
                Objects.equals(this.zonedDateTime, that.zonedDateTime) &&
                Objects.equals(this.zoneId, that.zoneId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.id, super.toAddresses, super.subject, super.message, zonedDateTime, zoneId);
    }

    @Override
    public String toString() {
        return "ELAScheduleMailDetails[" +
                "id=" + super.id + ", " +
                "toAddresses=" + super.toAddresses + ", " +
                "subject=" + super.subject + ", " +
                "message=" + super.message + ", " +
                "zonedDateTime=" + zonedDateTime + ", " +
                "zoneId=" + zoneId + ']';
    }

}
