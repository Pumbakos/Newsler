package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.newsler.api.exceptions.UnauthorizedException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.security.AlgorithmType;
import pl.newsler.security.NLIPasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RequiredArgsConstructor
class JWTAuthService {
    private final IUserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;
    private final JWTUtility jwtUtility;

    public String generateJWT(UserAuthModel userAuthModel) {
        final String email = passwordEncoder.decrypt(userAuthModel.email(), AlgorithmType.AES);
        final Optional<NLUser> optionalUser = userRepository.findByEmail(NLEmail.of(email));

        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException("User's credentials not valid", "Token not generated");
        }
        final NLUser user = optionalUser.get();

        if (userCredentialsValid(user, userAuthModel)) {
            return generateToken(optionalUser.get());
        } else {
            throw new UnauthorizedException("User's credentials not valid", "Token not generated");
        }
    }

    private boolean userCredentialsValid(NLUser user, UserAuthModel userAuthModel) {
        final String password = passwordEncoder.decrypt(userAuthModel.password(), AlgorithmType.AES);
        final String smtpAccount = passwordEncoder.decrypt(userAuthModel.smtpAccount(), AlgorithmType.AES);
        final String appKey = passwordEncoder.decrypt(userAuthModel.appKey(), AlgorithmType.AES);

        return (
                passwordEncoder.bCrypt().matches(password, user.getPassword())
                        && user.isEnabled()
                        && user.isCredentialsNonExpired()
                        && user.getSmtpAccount().getValue().equals(smtpAccount)
                        && user.getAppKey().getValue().equals(appKey)
        );
    }

    private String generateToken(NLUser user) {
        final Instant now = Instant.now();
        return jwtUtility.builder()
                .withJWTId(String.valueOf(JWTClaim.JWT_ID))
                .withKeyId(jwtUtility.hmac384().getSigningKeyId())
                .withIssuer(JWTClaim.ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(60L, ChronoUnit.MINUTES))
                .withClaim(JWTClaim.EMAIL, user.getEmail().getValue())
                .withClaim(JWTClaim.ROLE, user.getRole().toString())
//                .withClaim(JWTClaim.SMTP, user.getSmtpAccount().getValue())
//                .withClaim(JWTClaim.APP_KEY, user.getAppKey().getValue())
                .sign(jwtUtility.hmac384());
    }
}
