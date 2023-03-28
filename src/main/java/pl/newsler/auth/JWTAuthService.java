package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.newsler.commons.exception.UnauthorizedException;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLDUser;
import pl.newsler.components.user.NLUser;
import pl.newsler.security.NLIPasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.newsler.auth.AuthUserDetailService.AUTHORITIES_CLAIM_NAME;

@RequiredArgsConstructor
class JWTAuthService implements IJWTAuthService {
    private final IUserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;
    private final AuthUserDetailService authService;
    private final JWTUtility jwtUtility;

    @Override
    public String generateJWT(UserAuthModel userAuthModel) throws UnauthorizedException {
        final String email = userAuthModel.email();
        final NLEmail nlEmail = NLEmail.of(email);

        if (!nlEmail.validate()) {
            throw new UnauthorizedException("email", "invalid");
        }

        final UserDetails userDetails;
        try {
            userDetails = authService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            throw new UnauthorizedException("User's credentials not valid", "Token not generated");
        }

//        if (passwordEncoder.bCrypt().matches(userAuthModel.password(), userDetails.getPassword())) {
//            Map<String, String> claims = new HashMap<>();
//            claims.put("email", email);
//
//            String authorities = userDetails.getAuthorities().stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .collect(Collectors.joining(" "));
//            claims.put(AUTHORITIES_CLAIM_NAME, authorities);
//            claims.put("userId", String.valueOf(1));
//
//            String jwt = jwtHelper.createJwtForClaims(username, claims);
//            return new LoginResult(jwt);
//        }

        return generateToken((NLUser) userDetails);
    }

    private boolean credentialsValid(NLUser user, UserAuthModel userAuthModel) {
//        final String password = passwordEncoder.decrypt(userAuthModel.password());
//        final String email = passwordEncoder.decrypt(userAuthModel.email());

        return (passwordEncoder.bCrypt().matches(userAuthModel.password(), user.getPassword())
                && userAuthModel.email().equals(user.getEmail().getValue())
                && user.isEnabled()
                && user.isCredentialsNonExpired()
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
                .withClaim(JWTClaim.NAME, user.getFirstName().getValue())
                .withClaim(JWTClaim.ROLE, user.getRole().toString())
                .sign(jwtUtility.hmac384());
    }
}
