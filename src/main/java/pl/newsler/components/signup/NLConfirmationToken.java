package pl.newsler.components.signup;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.newsler.commons.model.NLId;
import pl.newsler.commons.model.NLToken;
import pl.newsler.commons.model.NLUuid;

import java.time.LocalDateTime;

@Entity
@Table(name = "CONFIRMATION_TOKENS", catalog = "NEWSLER", schema = "PUBLIC")
@Setter
@Getter
@NoArgsConstructor
public class NLConfirmationToken {
    @SequenceGenerator(
            name = "confirmation_token_seq",
            sequenceName = "confirmation_token_seq",
            allocationSize = 1
    )
    @EmbeddedId
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "ID")))
    @ToString.Exclude
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private NLId id;

    @NotNull
    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "TOKEN", nullable = false, unique = true, columnDefinition = "VARCHAR(255)")))
    private NLToken token;

    @NotNull
    @Column(name = "CREATION_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDate;

    @NotNull
    @Column(name = "EXPIRATION_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime expirationDate;

    @Column(name = "CONFIRMATION_DATE", columnDefinition = "TIMESTAMP")
    private LocalDateTime confirmationDate;

    @NotNull
    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "USER_UUID", nullable = false)))
    private NLUuid userUuid;

    NLConfirmationToken(final NLId id, final NLToken token, final NLUuid userUuid) {
        this.id = id;
        this.token = token;
        this.creationDate = LocalDateTime.now();
        this.expirationDate = creationDate.plusMinutes(15L);
        this.userUuid = userUuid;
    }
}
