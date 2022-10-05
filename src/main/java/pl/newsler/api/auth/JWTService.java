package pl.newsler.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
import pl.newsler.api.User;
import pl.newsler.api.UserRepository;
import pl.newsler.exceptions.implemenation.UnauthorizedException;
import pl.newsler.security.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@ApplicationScope
@RequiredArgsConstructor
public class JWTService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTConfiguration jwtConfiguration;

    public String generateJWT(UserAuthModel userAuthModel) {
        final String email = passwordEncoder.decrypt(userAuthModel.email(), AlgorithmType.AES);
        final Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException("User not found", "Token not generated");
        } else {
            final User user = optionalUser.get();
            final String password = passwordEncoder.decrypt(userAuthModel.password(), AlgorithmType.AES);

            if (passwordEncoder.bCrypt().matches(password, user.getPassword())) {
                return generateToken(user);
            } else {
                throw new UnauthorizedException("Invalid password", "Token not generated");
            }
        }
    }

    private String generateToken(User user) {
        final Instant now = Instant.now();
        return jwtConfiguration.builder()
                .withJWTId(new String(JWTClaim.JWT_ID))
                .withKeyId(jwtConfiguration.hmac384().getSigningKeyId())
                .withIssuer(JWTClaim.ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(60L, ChronoUnit.MINUTES))
                .withClaim(JWTClaim.ROLE, user.getRole().toString())
                .sign(jwtConfiguration.hmac384());
    }
}
