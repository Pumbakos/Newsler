package pl.newsler.auth;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.security.StubNLPasswordEncoder;
import pl.newsler.testcommons.environment.StubEnvironment;
import pl.newsler.testcommons.environment.StubEnvironmentCreationStrategy;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class StubJWTConfiguration {
    private final JWTConfiguration configuration;

    public StubJWTConfiguration(StubUserRepository userRepository, StubNLPasswordEncoder passwordEncoder, StubEnvironmentCreationStrategy strategy) {
        this.configuration = new JWTConfiguration(userRepository, passwordEncoder, new StubEnvironment(strategy));
    }

    public JWTUtility jwtUtility() {
        return configuration.jwtUtility(
                configuration.jwtValidationKey(configuration.keyStore()),
                configuration.jwtSigningKey(configuration.keyStore())
        );
    }

    public IJWTAuthService jwtAuthService(JWTUtility utility) {
        return configuration.jwtAuthService(utility, configuration.authUserDetailService());
    }

    public AuthUserDetailService authUserDetailService() {
        return configuration.authUserDetailService();
    }

    public KeyStore keyStore() {
        return configuration.keyStore();
    }

    public RSAPrivateKey jwtSigningKey() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        return configuration.jwtSigningKey(configuration.keyStore());
    }

    public RSAPublicKey jwtValidationKey() {
        return configuration.jwtValidationKey(configuration.keyStore());
    }

    public JwtDecoder jwtDecoder() {
        return configuration.jwtDecoder(jwtValidationKey());
    }

}
