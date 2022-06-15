package pl.palubiak.dawid.newsler.user.registration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.palubiak.dawid.newsler.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
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
    private User user;


    public ConfirmationToken(String token, LocalDateTime creationDate, LocalDateTime expirationDate, User user) {
        this.token = token;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.user = user;
    }
}
