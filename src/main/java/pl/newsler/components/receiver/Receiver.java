package pl.newsler.components.receiver;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLNickname;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.commons.model.NLVersion;
import pl.newsler.components.receiver.usecase.ReceiverGetResponse;

import java.io.Serializable;

@Entity
@Table(name = "RECEIVERS", catalog = "NEWSLER", schema = "PUBLIC")
@ToString
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Receiver implements Serializable {
    @Getter(AccessLevel.PACKAGE)
    @EmbeddedId
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "UUID", nullable = false, unique = true, updatable = false)))
    private NLUuid uuid;

    @Getter(AccessLevel.PACKAGE)
    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "VERSION", nullable = false)))
    private NLVersion version = IReceiverRepository.version;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "USER_UUID", nullable = false, updatable = false)))
    private NLUuid userUuid;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "EMAIL", nullable = false)))
    private NLEmail email;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "NICKNAME")))
    private NLNickname nickname;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "FIRST_NAME")))
    private NLFirstName firstName;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "LAST_NAME")))
    private NLLastName lastName;

    @Column(name = "AUTO_SAVED", nullable = false)
    private boolean autoSaved;

    public Receiver(final NLUuid userUuid, final NLEmail email, final NLNickname nickname, final NLFirstName firstName, final NLLastName lastName, boolean autoSaved) {
        this.userUuid = userUuid;
        this.email = email;
        this.nickname = nickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.autoSaved = autoSaved;
    }

    public ReceiverGetResponse toResponse() {
        return new ReceiverGetResponse(email.getValue(), nickname.getValue(), firstName.getValue(), lastName.getValue());
    }
}
