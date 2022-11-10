package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import pl.newsler.api.exceptions.UnauthorizedException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLDUser;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.UserDataNotFineException;
import pl.newsler.security.AlgorithmType;
import pl.newsler.security.NLIPasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RequiredArgsConstructor
class JWTAuthService implements IJWTAuthService{
    private final IUserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;
    private final JWTUtility jwtUtility;

    @Override
    public String generateJWT(UserAuthModel userAuthModel) throws IllegalArgumentException, UserDataNotFineException {
        final String email = passwordEncoder.decrypt(userAuthModel.email(), AlgorithmType.AES);
        final NLEmail nlEmail = NLEmail.of(email);
        if (!nlEmail.validate()) {
            throw new UnauthorizedException("email", "invalid");
        }

        final Optional<NLUser> optionalUser = userRepository.findByEmail(nlEmail);
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException("User's credentials not valid", "Token not generated");
        }

        final NLDUser user = NLDUser.of(optionalUser.get());
        if (!credentialsValid(user, userAuthModel)) {
            throw new UnauthorizedException("User's credentials invalid", "Token not generated");
        }
        return generateToken(user);
    }

    private boolean credentialsValid(NLDUser user, UserAuthModel userAuthModel) {
        final String password = passwordEncoder.decrypt(userAuthModel.password(), AlgorithmType.AES);
        final String email = passwordEncoder.decrypt(userAuthModel.email(), AlgorithmType.AES);

        return (passwordEncoder.bCrypt().matches(password, user.getPassword().getValue())
                && email.equals(user.getEmail().getValue())
                && user.isEnabled()
                && !user.isCredentialsExpired()
        );
    }

    private String generateToken(NLDUser user) {
        final Instant now = Instant.now();
        return jwtUtility.builder()
                .withJWTId(String.valueOf(JWTClaim.JWT_ID))
                .withKeyId(jwtUtility.hmac384().getSigningKeyId())
                .withIssuer(JWTClaim.ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(60L, ChronoUnit.MINUTES))
                .withClaim(JWTClaim.EMAIL, user.getEmail().getValue())
                .withClaim(JWTClaim.ROLE, user.getRole().toString())
                .withClaim(JWTClaim.NAME, user.getName().getValue())
                .sign(jwtUtility.hmac384());
    }
}
