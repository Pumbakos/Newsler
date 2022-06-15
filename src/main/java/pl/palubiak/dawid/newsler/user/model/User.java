package pl.palubiak.dawid.newsler.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.user.registration.EmailValidator;
import pl.palubiak.dawid.newsler.utils.DBModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "USERS", catalog = "PUBLIC")
public class User extends DBModel implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false, unique = true, columnDefinition = "NUMBER(10)")
    @ToString.Exclude
    private Long id;

    @NotBlank
    @Email(regexp = EmailValidator.EMAIL_PATTERN)
    @Column(name = "EMAIL", nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
    private String email;

    /**
     * If user is an organization it is preferred to use name as one String omitting lastName <br/>
     * i.e "name = Microsoft Corporation", "name = EmailLabs" <br/>
     * <strong>not</strong> "name = Microsoft", lastName = "Corporation"
     */
    @NotBlank
    @Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(50)")
    private String name;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private UserRole role;

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = false;

    @Column(name = "LOCKED", nullable = false)
    private Boolean locked = false;

    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL)
    private transient List<BusinessClient> businessClients;

    public User(String name, String lastName, String email, String password, UserRole role, String appKey, String secretKey, String smtpAccount) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.lastName = lastName;
        this.appKey = appKey;
        this.secretKey = secretKey;
        this.smtpAccount = smtpAccount;
        this.role = role;
    }

    public User(String name, String lastName, String email, String password, UserRole role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.lastName = lastName;
        this.role = role;
    }

    public User(String name, String email, String password, UserRole role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
