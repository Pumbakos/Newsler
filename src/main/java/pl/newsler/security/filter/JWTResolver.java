package pl.newsler.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.newsler.auth.JWTClaim;
import pl.newsler.commons.model.NLEmail;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class JWTResolver {
    static boolean resolveJWT(DecodedJWT jwt) {
        final Instant now = Instant.now();
        final String keyId = jwt.getId();
        final String issuer = jwt.getIssuer();
        final String email = jwt.getClaim(JWTClaim.EMAIL).asString();
        final String role = jwt.getClaim(JWTClaim.ROLE).asString();
        final String name = jwt.getClaim(JWTClaim.NAME).asString();
        final Instant expiration = jwt.getExpiresAtAsInstant();

        return (
                (StringUtils.isNotBlank(keyId)) && (keyId.equals(String.valueOf(JWTClaim.JWT_ID)))
                        && (StringUtils.isNotBlank(issuer)) && (issuer.equals(JWTClaim.ISSUER))
                        && (StringUtils.isNotBlank(email)) && (NLEmail.of(email).validate())
                        && (StringUtils.isNotBlank(role))
                        && (StringUtils.isNotBlank(name))
                        && (expiration != null) && (now.isBefore(expiration))
        );
    }
}
