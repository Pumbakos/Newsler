package pl.newsler.security.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.newsler.auth.JWTClaim;
import pl.newsler.components.user.NLDUser;
import pl.newsler.security.NLCredentials;
import pl.newsler.security.NLPrincipal;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class JWTFilterHelper {
    static boolean resolveJWT(DecodedJWT jwt) {
        final Instant now = Instant.now();
        final String keyId = jwt.getId();
        final String issuer = jwt.getIssuer();
        final String role = jwt.getClaim(JWTClaim.ROLE).asString();
        final String smtp = jwt.getClaim(JWTClaim.SMTP).asString();
        final String appKey = jwt.getClaim(JWTClaim.APP_KEY).asString();
        final Instant expiration = jwt.getExpiresAtAsInstant();

        return (
                StringUtils.isNotBlank(keyId) && keyId.equals(String.valueOf(JWTClaim.JWT_ID))
                        && StringUtils.isNotBlank(issuer) && issuer.equals(JWTClaim.ISSUER)
                        && StringUtils.isNotBlank(role)
                        && StringUtils.isNotBlank(smtp)
                        && StringUtils.isNotBlank(appKey)
                        && now.isBefore(expiration)
        );
    }

    static NLPrincipal createPrincipal(NLDUser user) {
        return new NLPrincipal(user.getId(), user.getEmail(), user.getSmtpAccount(), user.getAppKey());
    }

    static NLCredentials createCredentials(NLDUser user) {
        return new NLCredentials(user.getPassword().getValue(), user.getSecretKey());
    }
}
