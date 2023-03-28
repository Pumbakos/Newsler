package pl.newsler.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RequiredArgsConstructor
public class JWTUtility {
    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public Algorithm rsa384() {
        return Algorithm.RSA384(publicKey, privateKey);
    }

    public JWTCreator.Builder builder(String subject) {
        return JWT.create().withSubject(subject);
    }

    public String createJwtForClaims(String subject, Map<String, String> claims) {
        final Instant now = Instant.now();
        final JWTCreator.Builder jwtBuilder = builder(subject);

        claims.forEach(jwtBuilder::withClaim);

        return jwtBuilder
                .withJWTId(String.valueOf(JWTClaim.JWT_ID))
                .withKeyId(rsa384().getSigningKeyId())
                .withIssuer(JWTClaim.ISSUER)
                .withNotBefore(now)
                .withExpiresAt(now.plus(60L, ChronoUnit.MINUTES))
                .sign(rsa384());
    }
}
