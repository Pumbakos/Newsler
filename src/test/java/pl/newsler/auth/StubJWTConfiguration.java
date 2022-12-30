package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.security.StubNLIKeyProvider;
import pl.newsler.security.StubNLPasswordEncoder;

public class StubJWTConfiguration {
    private final JWTConfiguration configuration;

    public StubJWTConfiguration(StubUserRepository userRepository, StubNLIKeyProvider keyProvider, StubNLPasswordEncoder passwordEncoder) {
        this.configuration = new JWTConfiguration(userRepository, passwordEncoder, keyProvider);;
    }

    public JWTUtility jwtUtility() {
        return configuration.jwtUtility();
    }

    public IJWTAuthService jwtAuthService(JWTUtility utility) {
        return configuration.jwtAuthService(utility);
    }
}
