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
import pl.newsler.commons.models.NLEmailMessage;
import pl.newsler.commons.models.NLEmailStatus;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLStringValue;
import pl.newsler.commons.models.NLSubject;
import pl.newsler.commons.models.NLVersion;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "USER_MAILS", catalog = "NEWSLER", schema = "PUBLIC")
@ToString
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NLUserMail implements Serializable {
    @Serial
    private static final long serialVersionUID = 9009272834852329455L;

    @Getter(AccessLevel.PACKAGE)
    @EmbeddedId
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "ID")))
    private NLUuid id;

    @Getter(AccessLevel.PACKAGE)
    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "VERSION")))
    private NLVersion version;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "USER_ID")))
    private NLUuid userId;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "TO_ADDRESSES")))
    private NLStringValue toAddresses;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "CC")))
    private NLStringValue cc;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "BCC")))
    private NLStringValue bcc;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "SUBJECT", length = 128)))
    private NLSubject subject;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "MESSAGE", length = 5000)))
    private NLEmailMessage message;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private NLEmailStatus status;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "ERROR_MESSAGE", length = 512)))
    private NLStringValue errorMessage;

    public static NLUserMail of(NLUuid userId, MailDetails details) {
        return new NLUserMail(
                details.id(),
                MailRepository.version,
                userId,
                NLStringValue.of(Arrays.toString(details.toAddresses().toArray())),
                NLStringValue.of(Arrays.toString(details.cc() != null ? details.cc().toArray() : new String[0])),
                NLStringValue.of(Arrays.toString(details.bcc() != null ? details.bcc().toArray() : new String[0])),
                NLSubject.of(details.subject()),
                NLEmailMessage.of(details.message()),
                NLEmailStatus.QUEUED,
                NLStringValue.of("")
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
        final NLUserMail that = (NLUserMail) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
