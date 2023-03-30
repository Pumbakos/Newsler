package pl.newsler.components.emaillabs;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import pl.newsler.commons.model.NLEmailMessage;
import pl.newsler.commons.model.NLEmailStatus;
import pl.newsler.commons.model.NLExecutionDate;
import pl.newsler.commons.model.NLStringValue;
import pl.newsler.commons.model.NLSubject;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.commons.model.NLVersion;
import pl.newsler.components.emaillabs.executor.ELAInstantMailDetails;
import pl.newsler.components.emaillabs.executor.ELAScheduleMailDetails;
import pl.newsler.components.emaillabs.usecase.ELAGetMailResponse;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "USER_MAILS", catalog = "NEWSLER", schema = "PUBLIC")
@ToString
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ELAUserMail implements Serializable {
    @Serial
    private static final long serialVersionUID = 9009272834852329455L;

    @Getter(AccessLevel.PACKAGE)
    @EmbeddedId
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "UUID", nullable = false, unique = true, updatable = false)))
    private NLUuid uuid;

    @Getter(AccessLevel.PACKAGE)
    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "VERSION", nullable = false)))
    private NLVersion version;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "USER_UUID", nullable = false, updatable = false)))
    private NLUuid userId;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "TO_ADDRESSES", nullable = false, updatable = false)))
    private NLStringValue toAddresses;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "SUBJECT", length = 128, nullable = false, updatable = false)))
    private NLSubject subject;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "MESSAGE", length = 5000, nullable = false, updatable = false)))
    private NLEmailMessage message;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "EXECUTION_DATE", nullable = false)))
    private NLExecutionDate executionDate;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private NLEmailStatus status;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "ERROR_MESSAGE", length = 512)))
    private NLStringValue errorMessage;

    public static ELAUserMail of(NLUuid userId, ELAInstantMailDetails details) {
        return new ELAUserMail(
                details.id(),
                ELAMailRepository.version,
                userId,
                NLStringValue.of(Arrays.toString(details.toAddresses().toArray())),
                NLSubject.of(details.subject()),
                NLEmailMessage.of(details.message()),
                NLExecutionDate.of(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault())),
                NLEmailStatus.QUEUED,
                NLStringValue.of("")
        );
    }

    public static ELAUserMail of(NLUuid userId, ELAScheduleMailDetails details) {
        return new ELAUserMail(
                details.id(),
                ELAMailRepository.version,
                userId,
                NLStringValue.of(Arrays.toString(details.toAddresses().toArray())),
                NLSubject.of(details.subject()),
                NLEmailMessage.of(details.message()),
                NLExecutionDate.of(details.zonedDateTime()),
                NLEmailStatus.QUEUED,
                NLStringValue.of("")
        );
    }

    public ELADUserMail map() {
        return ELADUserMail.builder()
                .id(this.uuid)
                .toAddresses(this.toAddresses)
                .subject(this.subject)
                .message(this.message)
                .status(this.status)
                .errorMessage(this.errorMessage)
                .build();
    }


    public ELAGetMailResponse toResponse(String from) {
        return new ELAGetMailResponse(
                this.uuid.getValue(),
                from,
                Arrays.asList(this.toAddresses.getValue().split(",")),
                this.subject.getValue(),
                this.message.getValue(),
                this.status.name()
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        final ELAUserMail that = (ELAUserMail) o;
        return uuid != null && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
