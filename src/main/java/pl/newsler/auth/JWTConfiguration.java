package pl.newsler.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.internal.PropertiesUtil;
import pl.newsler.internal.exception.ConfigurationException;
import pl.newsler.security.NLIPasswordEncoder;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@ComponentScan
@Configuration(proxyBeanMethods = false)
class JWTConfiguration {
    private final IUserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;
    private final String keyStoreType;
    private final String keyStorePath;
    private final String keyStorePassword;
    private final String keyAlias;

    JWTConfiguration(final IUserRepository userRepository, final NLIPasswordEncoder passwordEncoder, final Environment env) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.keyStoreType = env.getProperty("newsler.security.keystore.key-store-type");
        this.keyStorePath = env.getProperty("newsler.security.keystore.key-store-path");
        this.keyStorePassword = env.getProperty("newsler.security.keystore.key-store-password");
        this.keyAlias = env.getProperty("newsler.security.keystore.key-alias");

        if (!PropertiesUtil.arePropsSet(keyStoreType, keyStorePath, keyStorePassword, keyAlias)) {
            throw new ConfigurationException("KeyStore properties not set properly");
        }
    }

    @Bean(name = "jwtUtility")
    JWTUtility jwtUtility(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        return new JWTUtility(publicKey, privateKey);
    }

    @Bean(name = "jwtAuthService")
    IJWTAuthService jwtAuthService(JWTUtility utility, AuthUserDetailService authUserDetailService) {
        return new JWTAuthService(passwordEncoder, authUserDetailService, utility);
    }

    @Bean(name = "authenticationProvider")
    AuthenticationProvider authenticationProvider() {
        return new NLAuthenticationProvider(userRepository);
    }

    @Bean(name = "authUserDetailService")
    AuthUserDetailService authUserDetailService() {
        return new AuthUserDetailService(userRepository);
    }

    @Bean(name = "keyStore")
    KeyStore keyStore() {
        try (final InputStream resourceAsStream = Files.newInputStream(Path.of(keyStorePath))) {
            final KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(resourceAsStream, keyStorePassword.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new ConfigurationException("Unable to load keystore", e);
        }
    }

    @Bean(name = "jwtSigningKey")
    RSAPrivateKey jwtSigningKey(KeyStore keyStore) {
        try {
            final Key key = keyStore.getKey(keyAlias, keyStorePassword.toCharArray());
            if (key instanceof RSAPrivateKey rsaKey) {
                return rsaKey;
            }
        } catch (Exception e) {
            throw new ConfigurationException("Unable to load RSA private key");
        }

        throw new ConfigurationException("Unable to load RSA private key, key not a instance of " + RSAPrivateKey.class.getSimpleName());
    }

    @Bean(name = "jwtValidationKey")
    RSAPublicKey jwtValidationKey(KeyStore keyStore) {
        try {
            final Certificate certificate = keyStore.getCertificate(keyAlias);
            final PublicKey publicKey = certificate.getPublicKey();

            if (publicKey instanceof RSAPublicKey key) {
                return key;
            }
        } catch (Exception e) {
            throw new ConfigurationException("Unable to load RSA public key");
        }
        throw new ConfigurationException("Unable to load RSA public key, key not a instance of " + RSAPublicKey.class.getSimpleName());
    }

    @Bean(name = "jwtDecoder")
    JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
