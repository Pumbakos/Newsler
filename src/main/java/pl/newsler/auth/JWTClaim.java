package pl.newsler.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JWTClaim {
    public static final String ISSUER = "Newsler";
    public static final String ROLE = "ROLE";
    public static final String EMAIL = "EMAIL";
    public static final String SMTP = "SMTP_ACCOUNT";
    public static final String APP_KEY = "APP_KEY";
    public static final char[] JWT_ID = new char[]{
            67, 100, 103, 97, 98, 110, 37, 35, 73, 43, 47, 57, 61, 107, 51, 69, 69,
            106, 104, 125, 49, 86, 60, 54, 67, 35, 104, 124, 110, 38, 58, 102
    };
}
