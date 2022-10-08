package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.newsler.api.exceptions.UnauthorizedException;
import pl.newsler.api.user.User;
import pl.newsler.api.user.UserRepository;
import pl.newsler.security.AlgorithmType;
import pl.newsler.security.NLPasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JWTAuthService {
    private final UserRepository userRepository;
    private final NLPasswordEncoder passwordEncoder;
    private final JWTUtility jwtUtility;

    public String generateJWT(UserAuthModel userAuthModel) {
        final String email = passwordEncoder.decrypt(userAuthModel.email(), AlgorithmType.AES);
        final Optional<User> optionalUser = userRepository.findByEmail(email);

        if (userCredentialsValid(optionalUser, userAuthModel)) {
            return generateToken(optionalUser.get());
        } else {
            throw new UnauthorizedException("User's credentials not valid", "Token not generated");
        }
    }

    private boolean userCredentialsValid(Optional<User> optionalUser, UserAuthModel userAuthModel) {
        if (optionalUser.isEmpty()) {
            return false;
        }

        final User user = optionalUser.get();
        final String password = passwordEncoder.decrypt(userAuthModel.password(), AlgorithmType.AES);
        final String smtpAccount = passwordEncoder.decrypt(userAuthModel.smtpAccount(), AlgorithmType.AES);
        final String appKey = passwordEncoder.decrypt(userAuthModel.appKey(), AlgorithmType.AES);

        return (
                passwordEncoder.bCrypt().matches(password, user.getPassword())
                        && user.isAccountNonLocked()
                        && user.isCredentialsNonExpired()
                        && user.getSmtpAccount().equals(smtpAccount)
                        && user.getAppKey().equals(appKey)
        );
    }

    private String generateToken(User user) {
        final Instant now = Instant.now();
        return jwtUtility.builder()
                .withJWTId(String.valueOf(JWTClaim.JWT_ID))
                .withKeyId(jwtUtility.hmac384().getSigningKeyId())
                .withIssuer(JWTClaim.ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(60L, ChronoUnit.MINUTES))
                .withClaim(JWTClaim.EMAIL, user.getEmail())
                .withClaim(JWTClaim.ROLE, user.getRole().toString())
                .withClaim(JWTClaim.SMTP, user.getSmtpAccount())
                .withClaim(JWTClaim.APP_KEY, user.getAppKey())
                .sign(jwtUtility.hmac384());
    }
}
