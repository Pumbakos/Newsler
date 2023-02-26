package pl.newsler.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.newsler.components.user.NLUser;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JWTTestResolver {
    public static boolean resolve(DecodedJWT jwt, final NLUser user) {
        return JWTResolver.resolveJWT(jwt, user);
    }
}
