package pl.newsler.components.user;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.commons.models.NLType;
import pl.newsler.commons.models.NLVersion;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;

@ToString
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class NLUser implements UserDetails {
    @Serial
    private static final long serialVersionUID = -1087455812902755879L;
    @Getter(AccessLevel.PACKAGE)
    private NLId id;

    @Getter(AccessLevel.PACKAGE)
    private NLVersion version;

    private NLEmail email;

    /**
     * If user is an organization it is preferred to use name as one String omitting lastName <br/>
     * i.e "name = Microsoft Corporation", "name = EmailLabs" <br/>
     * <strong>not</strong> "name = Microsoft", lastName = "Corporation"
     */
    private NLFirstName firstName;

    private NLPassword password;

    private NLLastName lastName;

    /**
     * <a href="https://panel.emaillabs.net.pl/en/site/api">You can find APP KEY here</a><br/>
     * <strong>Note: </strong> it is required to have an active account on emaillabs.io and to be logged in
     */
    private NLAppKey appKey;

    /**
     * <a href="https://panel.emaillabs.net.pl/en/site/api">You can find SECRET KEY here</a><br/>
     * <strong>Note: </strong> it is required to have an active account on emaillabs.io and to be logged in
     */
    private NLSecretKey secretKey;

    /**
     * <a href="https://panel.emaillabs.net.pl/pl/smtp">You can find SMTP ACCOUNT here</a><br/>
     * <strong>Note: </strong> it is required to have an active account on emaillabs.io and to be logged in
     */
    private NLSmtpAccount smtpAccount;

    private NLType role;

    private Boolean enabled = false;

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
                .id(id)
                .email(email)
                .name(firstName)
                .lastName(lastName)
                .password(password)
                .smtpAccount(smtpAccount)
                .secretKey(secretKey)
                .appKey(appKey)
                .role(role)
                .credentialsExpired(false)
                .enabled(enabled)
                .build();
    }
}
