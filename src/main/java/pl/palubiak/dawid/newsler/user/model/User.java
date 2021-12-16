package pl.palubiak.dawid.newsler.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false, unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @NotBlank
    @Email(regexp = "[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}")
    @Column(nullable = false, name = "EMAIL", columnDefinition = "VARCHAR(255)")
    private String email;

    @NotBlank
    @Column(name = "PASSWORD", nullable = false, columnDefinition = "VARCHAR(255)")
    private String password;

    /**
     * <a href="https://panel.emaillabs.net.pl/en/site/api">You can find APP KEY here</a><br/>
     * <strong>Note: </strong> it is required to have an active account on emaillabs.io and to be logged in
     */
    @NotBlank
    @Column(nullable = false, name = "APP_KEY", columnDefinition = "VARCHAR(255)")
    private String APP_KEY;

    /**
     * <a href="https://panel.emaillabs.net.pl/en/site/api">You can find SECRET KEY here</a><br/>
     * <strong>Note: </strong> it is required to have an active account on emaillabs.io and to be logged in
     */
    @NotBlank
    @Column(nullable = false, name = "SECRET_KEY", columnDefinition = "VARCHAR(255)")
    private String SECRET_KEY;

    /**
     * If user is an organization it is preferred to use name as one String omitting lastName <br/>
     * i.e "name = Microsoft Corporation", "name = EmailLabs" <br/>
     * <strong>not</strong> "name = Microsoft", lastName = "Corporation"
     */
    @NotBlank
    @Column(nullable = false, name = "FIRST_NAME", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "LAST_NAME", columnDefinition = "VARCHAR(50)")
    private String lastName;

    /**
     * <a href="https://panel.emaillabs.net.pl/pl/smtp">You can find SMTP ACCOUNT here</a><br/>
     * <strong>Note: </strong> it is required to have an active account on emaillabs.io and to be logged in
     */
    @NotBlank
    @Column(nullable = false, name = "SMTP_ACCOUNT", columnDefinition = "VARCHAR(255)")
    private String smtpAccount;
}
