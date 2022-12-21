package pl.newsler.components.mail;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import pl.newsler.commons.models.NLEmailStatus;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLMessage;
import pl.newsler.commons.models.NLStringValue;
import pl.newsler.commons.models.NLSubject;
import pl.newsler.commons.models.NLVersion;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "USER_MAILS", catalog = "NEWSLER", schema = "PUBLIC")
@ToString
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NLUserMail implements Serializable {
    @Serial
    private static final long serialVersionUID = 9009272834852329455L;

    @Getter(AccessLevel.PACKAGE)
    @EmbeddedId
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "ID")))
    private NLId id;

    @Getter(AccessLevel.PACKAGE)
    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "VERSION")))
    private NLVersion version;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "USER_ID")))
    private NLId userId;

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
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "SUBJECT")))
    private NLSubject subject;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "MESSAGE")))
    private NLMessage message;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private NLEmailStatus status;

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
