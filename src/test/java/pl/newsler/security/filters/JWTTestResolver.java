package pl.newsler.security.filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JWTTestResolver {
    public static boolean resolve(DecodedJWT jwt){
        return JWTResolver.resolveJWT(jwt);
    }
}
