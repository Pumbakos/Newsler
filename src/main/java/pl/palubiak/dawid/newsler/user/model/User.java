package pl.palubiak.dawid.newsler.user.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.utils.DBModel;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "USERS")
public class User extends DBModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false, unique = true, columnDefinition = "NUMBER(10)")
    @ToString.Exclude
    private long id;

    @NotBlank
    @Email(regexp = "[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}")
    @Column(name = "EMAIL", nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
    private String email;

    /**
     * If user is an organization it is preferred to use name as one String omitting lastName <br/>
     * i.e "name = Microsoft Corporation", "name = EmailLabs" <br/>
     * <strong>not</strong> "name = Microsoft", lastName = "Corporation"
     */
    @NotBlank
    @Column(name = "NAME", nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
    private String name;

//    @ValidPassword passay?
    @NotBlank
    @Column(name = "PASSWORD", nullable = false, columnDefinition = "VARCHAR(255)")
    private String password;

    @Column(name = "LAST_NAME", columnDefinition = "VARCHAR(50)")
    private String lastName;

    /**
     * <a href="https://panel.emaillabs.net.pl/en/site/api">You can find APP KEY here</a><br/>
     * <strong>Note: </strong> it is required to have an active account on emaillabs.io and to be logged in
     */
    @Column(name = "APP_KEY", unique = true, columnDefinition = "VARCHAR(255)")
    private String appKey;

    /**
     * <a href="https://panel.emaillabs.net.pl/en/site/api">You can find SECRET KEY here</a><br/>
     * <strong>Note: </strong> it is required to have an active account on emaillabs.io and to be logged in
     */
    @Column(name = "SECRET_KEY", unique = true, columnDefinition = "VARCHAR(255)")
    private String secretKey;

    /**
     * <a href="https://panel.emaillabs.net.pl/pl/smtp">You can find SMTP ACCOUNT here</a><br/>
     * <strong>Note: </strong> it is required to have an active account on emaillabs.io and to be logged in
     */
    @Column(name = "SMTP_ACCOUNT", unique = true, columnDefinition = "VARCHAR(255)")
    private String smtpAccount;

    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL)
    List<BusinessClient> businessClients;
}
