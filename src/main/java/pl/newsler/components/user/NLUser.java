package pl.newsler.components.user;

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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.newsler.commons.model.NLAppKey;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.commons.model.NLStringValue;
import pl.newsler.commons.model.NLToken;
import pl.newsler.commons.model.NLUserType;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.commons.model.NLVersion;
import pl.newsler.components.user.usecase.UserGetResponse;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Entity
@Table(name = "USERS", catalog = "NEWSLER", schema = "PUBLIC")
@ToString
@Getter
@Setter
public class NLUser implements UserDetails {
    @Serial
    private static final long serialVersionUID = -1087455812902755879L;

    @Getter(AccessLevel.PACKAGE)
    @EmbeddedId
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "UUID")))
    private NLUuid uuid;

    @Getter(AccessLevel.PACKAGE)
    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "VERSION")))
    private NLVersion version;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "EMAIL")))
    private NLEmail email;

    /**
     * If user is an organization it is preferred to use name as one String omitting lastName <br/>
     * i.e "name = Microsoft Corporation", "name = EmailLabs" <br/>
     * <strong>not</strong> "name = Microsoft", lastName = "Corporation"
     */
    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "FIRST_NAME", nullable = false)))
    private NLFirstName firstName;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "LAST_NAME")))
    private NLLastName lastName;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "PASSWORD")))
    private NLPassword password;

    /**
     * <a href="https://panel.emaillabs.net.pl/en/site/api">You can find APP KEY here</a><br/>
     * <strong>Note: </strong> it is required to have an active account on emaillabs.io and to be logged in
     */
    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "APP_KEY")))
    private NLAppKey appKey;

    /**
     * <a href="https://panel.emaillabs.net.pl/en/site/api">You can find SECRET KEY here</a><br/>
     * <strong>Note: </strong> it is required to have an active account on emaillabs.io and to be logged in
     */
    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "SECRET_KEY")))
    private NLSecretKey secretKey;

    /**
     * <a href="https://panel.emaillabs.net.pl/pl/smtp">You can find SMTP ACCOUNT here</a><br/>
     * <strong>Note: </strong> it is required to have an active account on emaillabs.io and to be logged in
     */
    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "SMTP_ACCOUNT")))
    private NLSmtpAccount smtpAccount;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "CANCELLATION_TOKEN")))
    private NLToken cancellationToken;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "value", column = @Column(name = "DEFAULT_TEMPLATE_ID")))
    private NLStringValue defaultTemplateId = NLStringValue.of("");

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    private NLUserType role;

    @Column(name = "ENABLED")
    private Boolean enabled = false;

    @Column(name = "LOCKED")
    private Boolean locked = false;

    public NLPassword getNLPassword() {
        return password;
    }

    @Override
    public String getPassword() {
        return this.password.getValue();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role.name()));
    }

    protected NLUser() {
        version = UserRepository.version;
        email = NLEmail.of("");
        firstName = NLFirstName.of("");
        lastName = NLLastName.of("");
        password = NLPassword.of("");
        appKey = NLAppKey.of("");
        secretKey = NLSecretKey.of("");
        smtpAccount = NLSmtpAccount.of("");
        cancellationToken = NLToken.of(UUID.randomUUID().toString().concat("-").concat(UUID.randomUUID().toString()));
    }

    @Override
    public String getUsername() {
        return this.email.getValue();
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

    public NLDUser map() {
        return NLDUser.builder()
                .id(uuid)
                .email(email)
                .name(firstName)
                .lastName(lastName)
                .password(password)
                .smtpAccount(smtpAccount)
                .secretKey(secretKey)
                .appKey(appKey)
                .role(role)
                .credentialsNonExpired(false)
                .enabled(enabled)
                .build();
    }

    public UserGetResponse truncate() {
        return new UserGetResponse(uuid.getValue(), email.getValue(), firstName.getValue(), lastName.getValue(), smtpAccount.getValue(), secretKey.getValue(), appKey.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final NLUser nlUser = (NLUser) o;

        return new EqualsBuilder()
                .append(uuid, nlUser.uuid)
                .append(version, nlUser.version)
                .append(email, nlUser.email)
                .append(firstName, nlUser.firstName)
                .append(password, nlUser.password)
                .append(lastName, nlUser.lastName)
                .append(appKey, nlUser.appKey)
                .append(secretKey, nlUser.secretKey)
                .append(smtpAccount, nlUser.smtpAccount)
                .append(role, nlUser.role)
                .append(enabled, nlUser.enabled)
                .append(locked, nlUser.locked)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(uuid).append(version)
                .append(email)
                .append(firstName)
                .append(password)
                .append(lastName)
                .append(appKey)
                .append(secretKey)
                .append(smtpAccount)
                .append(role)
                .append(enabled)
                .append(locked)
                .toHashCode();
    }
}
