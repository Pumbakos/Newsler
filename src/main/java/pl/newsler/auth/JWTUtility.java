package pl.newsler.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import pl.newsler.security.NLIKeyProvider;
import pl.newsler.security.NLPublicAlias;

@RequiredArgsConstructor
public class JWTUtility {
    private final NLIKeyProvider keyProvider;

    public Algorithm hmac384() {
        return Algorithm.HMAC384(keyProvider.getKey(NLPublicAlias.PE_PASSWORD));
    }

    public JWTCreator.Builder builder() {
        return JWT.create();
    }
}
