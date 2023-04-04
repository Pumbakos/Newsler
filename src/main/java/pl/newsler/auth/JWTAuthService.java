package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.newsler.commons.exception.UnauthorizedException;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.components.user.NLUser;
import pl.newsler.security.NLIPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class JWTAuthService implements IJWTAuthService {
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

        if (passwordEncoder.bCrypt().matches(userAuthModel.password(), userDetails.getPassword())) {
            final Map<String, String> claims = new HashMap<>();
            final String authorities = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            final NLUser user = (NLUser) userDetails;
            if (!user.isUserActive()) {
                throw new UnauthorizedException("User not active", "Token not generated");
            }

            claims.put("username", email);
            claims.put("authorities", authorities);
            claims.put("uuid", user.map().getUuid().getValue());

            return jwtUtility.createJwtForClaims(email, claims);
        }

        throw new UnauthorizedException("User's credentials not valid", "Token not generated");
    }
}
