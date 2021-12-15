package pl.palubiak.dawid.newsler.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Entity
@Table(name = "USER")
public class User {
    @Id
    @Column(name = "ID", nullable = false, updatable = false, unique = true)
    private String id;

    /**
     * <a>You can find your APP KEY here</a>
     */
    @NotBlank
    @Column(nullable = false, name = "APP_KEY", columnDefinition = "VARCHAR(50)")
    private String APP_KEY;

    /**
     * <a>You can find your SECRET KEY here</a>
     */
    @NotBlank
    @Column(nullable = false, name = "SECRET_KEY", columnDefinition = "VARCHAR(50)")
    private String SECRET_KEY;

    /**
     * If user is an organization it is preferred to use name as one String omitting lastName empty
     * i.e "Microsoft Corporation", "EmailLabs"
     */
    @NotBlank
    @Column(nullable = false, name = "FIRST_NAME", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "LAST_NAME", columnDefinition = "VARCHAR(50)")
    private String lastName;

    /**
     *
     */
    @NotBlank
    @Column(nullable = false, name = "SMTP_ACCOUNT", columnDefinition = "VARCHAR(50)")
    private String smtpAccount;

    //FIXME: validate email
    @NotBlank
    @Email
    @Column(nullable = false, name = "EMAIL", columnDefinition = "VARCHAR(50)")
    private String email;
}
