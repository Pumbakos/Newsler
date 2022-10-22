package pl.newsler.models.tokens;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.newsler.models.user.NLUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "CONFIRMATION_TOEKN", schema = "PUBLIC")
public class ConfirmationToken {@SequenceGenerator(
        name = "confirmation_token_seq",
        sequenceName = "confirmation_token_seq",
        allocationSize = 1
)
@Id
@ToString.Exclude
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private long id;

    @NotNull
    @Column(name = "TOKEN", nullable = false,columnDefinition = "VARCHAR(255)")
    private String token;

    @NotNull
    @Column(name = "CREATION_DATE", nullable = false,columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDate;

    @NotNull
    @Column(name = "EXPIRATION_DATE", nullable = false,columnDefinition = "TIMESTAMP")
    private LocalDateTime expirationDate;

    @Column(name = "CONFIRMATION_DATE", columnDefinition = "TIMESTAMP")
    private LocalDateTime confirmationDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private NLUser user;


    public ConfirmationToken(String token, LocalDateTime creationDate, LocalDateTime expirationDate, NLUser user) {
        this.token = token;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.user = user;
    }
}
