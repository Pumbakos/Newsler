package pl.newsler.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import pl.newsler.auth.IJWTAuthService;
import pl.newsler.auth.JWTClaim;
import pl.newsler.auth.JWTUtility;
import pl.newsler.auth.StubJWTConfiguration;
import pl.newsler.auth.UserAuthModel;
import pl.newsler.commons.TestConfigurationException;
import pl.newsler.commons.exception.InvalidTokenException;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.security.StubNLPasswordEncoder;
import pl.newsler.testcommons.environment.KeyStorePropsStrategy;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static pl.newsler.testcommons.TestUserUtils.domain;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.username;

@SuppressWarnings({"java:S5778"})
class JWTFilterTest {
    private final StubUserRepository userRepository = new StubUserRepository();
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final StubAuthenticationManager authenticationManager = new StubAuthenticationManager();
    private final StubJWTConfiguration configuration = new StubJWTConfiguration(userRepository, passwordEncoder, new KeyStorePropsStrategy());
    private final JWTUtility utility = configuration.jwtUtility();
    private final IJWTAuthService service = configuration.jwtAuthService(utility);
    private final TestUserFactory factory = new TestUserFactory();
    private final JWTVerifier verifier = JWT.require(utility.rsa384()).build();
    private final JWTFilter filter = new JWTFilter(authenticationManager, configuration.authUserDetailService(), utility, "/v1/api/auth/", "/v1/api/subscription/cancel");

    @BeforeEach
    void beforeEach() {
        NLUuid standardId = NLUuid.of(UUID.randomUUID());
        factory.standard().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.standard_plainPassword())));
        factory.standard().setUuid(standardId);
        userRepository.save(factory.standard());

        NLUuid dashedId = NLUuid.of(UUID.randomUUID());
        factory.dashed().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dashed_plainPassword())));
        factory.dashed().setUuid(dashedId);
        userRepository.save(factory.dashed());

        NLUuid dottedId = NLUuid.of(UUID.randomUUID());
        factory.dotted().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dotted_plainPassword())));
        factory.dotted().setUuid(dottedId);
        userRepository.save(factory.dotted());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void shouldResolveJWT() {
        NLUser standardUser = factory.standard();
        UserAuthModel model = new UserAuthModel(
                standardUser.getEmail().getValue(),
                factory.standard_plainPassword()
        );
        String token = service.generateJWT(model);
        DecodedJWT decodedJWT = verify(token);
        JWTResolver.resolveJWT(decodedJWT, standardUser);

        Assertions.assertEquals(String.valueOf(JWTClaim.JWT_ID), decodedJWT.getId());
        Assertions.assertEquals(JWTClaim.ISSUER, decodedJWT.getIssuer());
        Assertions.assertEquals(standardUser.getEmail().getValue(), decodedJWT.getClaim(JWTClaim.EMAIL).asString());
        Assertions.assertEquals(standardUser.map().getUuid().getValue(), decodedJWT.getClaim(JWTClaim.UUID).asString());
        Assertions.assertEquals(standardUser.getRole().toString(), decodedJWT.getClaim(JWTClaim.AUTHORITIES).asString());
    }

    @Test
    void shouldNotResolveJWT() {
        NLUser standardUser = factory.standard();
        Instant now = Instant.now();
        Assertions.assertFalse(
                JWTResolver.resolveJWT(
                        verify(generateToken(now, null, null, null, null, null, null)),
                        standardUser
                )
        );
        Assertions.assertFalse(
                JWTResolver.resolveJWT(
                        verify(generateToken(now, "", "", "", "", "", "")),
                        standardUser
                )
        );
        Assertions.assertFalse(
                JWTResolver.resolveJWT(
                        verify(generateToken(now, "test", "test", "test", "test", "test", "test")),
                        standardUser
                )
        );

        Instant after = Instant.now(Clock.offset(Clock.system(ZoneId.systemDefault()), Duration.of(-61L, ChronoUnit.MINUTES)));
        Assertions.assertThrows(
                InvalidTokenException.class,
                () -> JWTResolver.resolveJWT(
                        verify(generateToken(after, "test", "test", "test", "test", "test", "test")),
                        standardUser
                )
        );
    }

    @Test
    void shouldDoFilterInternal() {
        NLUser standardUser = factory.standard();
        UserAuthModel model = new UserAuthModel(
                standardUser.getEmail().getValue(),
                factory.standard_plainPassword()
        );
        String token = service.generateJWT(model);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, token);
        Assertions.assertDoesNotThrow(() -> filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain()));
    }

    @Test
    void shouldDoFilterInternal_BearerPrefix() {
        NLUser standardUser = factory.standard();
        UserAuthModel model = new UserAuthModel(
                standardUser.getEmail().getValue(),
                factory.standard_plainPassword()
        );
        String token = service.generateJWT(model);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        request.setRequestURI("/v1/api/auth/jwt");
        Assertions.assertDoesNotThrow(() -> filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "", "Bearer ", "     ", "Bearer      "})
    void shouldNotDoFilterInternal_BlankTokens(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, token);
        Assertions.assertThrows(InvalidTokenException.class, () -> filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain()));
    }

    @Test
    void shouldNotDoFilterInternal_EmailDoesNotMatchRegExp() {
        String token = generateToken(JWTClaim.ISSUER, "test.d'amaro@.com.dev", firstName(), "USER");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, token);
        Assertions.assertThrows(InvalidTokenException.class, () -> filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain()));
    }

    @Test
    void shouldNotDoFilterInternal_UserNotExists() {
        String token = generateToken(JWTClaim.ISSUER, String.format("%s@%s.dev", username(), domain()), firstName(), "USER");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, token);
        Assertions.assertThrows(InvalidTokenException.class, () -> filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain()));
    }

    @Test
    void shouldNotDoFilterInternal_TokenNotResolved_WrongIssuer() {
        NLUser dashedUser = factory.dashed();
        String token = generateToken(
                JWTClaim.ISSUER + " Test",
                dashedUser.getEmail().getValue(),
                dashedUser.getFirstName().getValue(),
                dashedUser.getRole().toString()
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, token);
        Assertions.assertThrows(InvalidTokenException.class, () -> filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain()));
    }

    private DecodedJWT verify(String token) {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException("Token", "Invalid token");
        }
    }

    private Algorithm rsa384() {
        try {
            return Algorithm.RSA384(configuration.jwtValidationKey(), configuration.jwtSigningKey());
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
            throw new TestConfigurationException(e);
        }
    }

    private String generateToken(Instant now, String jwtId, String keyId, String issuer, String email, String name, String role) {
        return utility.builder(email)
                .withJWTId(jwtId)
                .withKeyId(keyId)
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(60L, ChronoUnit.MINUTES))
                .withClaim(JWTClaim.EMAIL, email)
                .withClaim(JWTClaim.UUID, name)
                .withClaim(JWTClaim.AUTHORITIES, role)
                .sign(utility.rsa384());
    }

    private String generateToken(String issuer, String email, String name, String role) {
        final Instant now = Instant.now();
        return JWT.create()
                .withJWTId(String.valueOf(JWTClaim.JWT_ID))
                .withKeyId(rsa384().getSigningKeyId())
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(60L, ChronoUnit.MINUTES))
                .withClaim(JWTClaim.EMAIL, email)
                .withClaim(JWTClaim.UUID, name)
                .withClaim(JWTClaim.AUTHORITIES, role)
                .sign(rsa384());
    }
}
