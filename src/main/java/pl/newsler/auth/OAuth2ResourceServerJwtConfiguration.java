package pl.newsler.auth;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.IssuerUriCondition;
import org.springframework.boot.autoconfigure.security.oauth2.resource.KeyValueCondition;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.SupplierJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.CollectionUtils;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Configures a {@link JwtDecoder} when a JWK Set URI, OpenID Connect Issuer URI or Public
 * Key configuration is available. Also configures a {@link SecurityFilterChain} if a
 * {@link JwtDecoder} bean is found.
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class OAuth2ResourceServerJwtConfiguration {
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(JwtDecoder.class)
    static class JwtDecoderConfiguration {
        private final OAuth2ResourceServerProperties.Jwt properties;

        JwtDecoderConfiguration(OAuth2ResourceServerProperties properties) {
            this.properties = properties.getJwt();
        }

        @Bean
        @ConditionalOnProperty(name = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri")
        JwtDecoder jwtDecoderByJwkKeySetUri() {
            final NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(this.properties.getJwkSetUri())
                    .jwsAlgorithms(this::jwsAlgorithms)
                    .build();
            final String issuerUri = this.properties.getIssuerUri();
            final Supplier<OAuth2TokenValidator<Jwt>> defaultValidator = (issuerUri != null)
                    ? () -> JwtValidators.createDefaultWithIssuer(issuerUri) : JwtValidators::createDefault;
            nimbusJwtDecoder.setJwtValidator(getValidators(defaultValidator));
            return nimbusJwtDecoder;
        }

        private void jwsAlgorithms(Set<SignatureAlgorithm> signatureAlgorithms) {
            for (String algorithm : this.properties.getJwsAlgorithms()) {
                signatureAlgorithms.add(SignatureAlgorithm.from(algorithm));
            }
        }

        private OAuth2TokenValidator<Jwt> getValidators(Supplier<OAuth2TokenValidator<Jwt>> defaultValidator) {
            final OAuth2TokenValidator<Jwt> defaultValidators = defaultValidator.get();
            final List<String> audiences = this.properties.getAudiences();
            if (CollectionUtils.isEmpty(audiences)) {
                return defaultValidators;
            }
            final List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
            validators.add(defaultValidators);
            validators.add(new JwtClaimValidator<List<String>>(JwtClaimNames.AUD,
                    (aud) -> aud != null && !Collections.disjoint(aud, audiences)));
            return new DelegatingOAuth2TokenValidator<>(validators);
        }

        @Bean
        @Conditional(KeyValueCondition.class)
        JwtDecoder jwtDecoderByPublicKeyValue() throws Exception {
            final RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(getKeySpec(this.properties.readPublicKey())));
            final NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey)
                    .signatureAlgorithm(SignatureAlgorithm.from(exactlyOneAlgorithm()))
                    .build();
            jwtDecoder.setJwtValidator(getValidators(JwtValidators::createDefault));
            return jwtDecoder;
        }

        private byte[] getKeySpec(String keyValue) {
            keyValue = keyValue.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
            return Base64.getMimeDecoder().decode(keyValue);
        }

        private String exactlyOneAlgorithm() {
            final List<String> algorithms = this.properties.getJwsAlgorithms();
            int count = (algorithms != null) ? algorithms.size() : 0;
            if (count != 1) {
                throw new IllegalStateException(
                        "Creating a JWT decoder using a public key requires exactly one JWS algorithm but " + count
                                + " were configured");
            }
            return algorithms.get(0);
        }

        @Bean
        @Conditional(IssuerUriCondition.class)
        SupplierJwtDecoder jwtDecoderByIssuerUri() {
            return new SupplierJwtDecoder(() -> {
                String issuerUri = this.properties.getIssuerUri();
                NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);
                jwtDecoder.setJwtValidator(getValidators(() -> JwtValidators.createDefaultWithIssuer(issuerUri)));
                return jwtDecoder;
            });
        }
    }
}