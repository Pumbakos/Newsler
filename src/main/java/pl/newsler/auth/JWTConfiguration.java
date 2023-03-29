package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.NLIPasswordEncoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@ComponentScan
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
class JWTConfiguration {
    private final IUserRepository userRepository;
    private final NLIPasswordEncoder passwordEncoder;

    @Value("${newsler.security.keystore.key-store-type}")
    private String keyStoreType;

    @Value("${newsler.security.keystore.key-store-path}")
    private String keyStorePath;

    @Value("${newsler.security.keystore.key-store-password}")
    private String keyStorePassword;

    @Value("${newsler.security.keystore.key-alias}")
    private String keyAlias;

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
        try {
            final KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            final InputStream resourceAsStream = new FileInputStream(keyStorePath);
            keyStore.load(resourceAsStream, keyStorePassword.toCharArray());
            return keyStore;
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
            log.error("Unable to load keystore: {}", keyStorePath, e);
        }

        throw new IllegalArgumentException("Unable to load keystore");
    }

    @Bean(name = "jwtSigningKey")
    RSAPrivateKey jwtSigningKey(KeyStore keyStore) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        final Key key = keyStore.getKey(keyAlias, keyStorePassword.toCharArray());
        if (key instanceof RSAPrivateKey rsaKey) {
            return rsaKey;
        }

        throw new IllegalArgumentException("Unable to load private key");
    }

    @Bean(name = "jwtValidationKey")
    RSAPublicKey jwtValidationKey(KeyStore keyStore) {
        try {
            final Certificate certificate = keyStore.getCertificate(keyAlias);
            final PublicKey publicKey = certificate.getPublicKey();

            if (publicKey instanceof RSAPublicKey key) {
                return key;
            }
        } catch (KeyStoreException e) {
            log.error("Unable to load private key from keystore: {}", keyStorePath, e);
        }

        throw new IllegalArgumentException("Unable to load RSA public key");
    }

    @Bean(name = "jwtDecoder")
    JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
