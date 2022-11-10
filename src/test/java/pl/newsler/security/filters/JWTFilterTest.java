package pl.newsler.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import pl.newsler.api.exceptions.UnauthorizedException;
import pl.newsler.auth.IJWTAuthService;
import pl.newsler.auth.JWTClaim;
import pl.newsler.auth.JWTConfiguration;
import pl.newsler.auth.JWTUtility;
import pl.newsler.auth.UserAuthModel;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.components.user.MockUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.UserFactory;
import pl.newsler.security.MockNLIKeyProvider;
import pl.newsler.security.MockNLPasswordEncoder;
import pl.newsler.security.NLPublicAlias;

import javax.ws.rs.core.HttpHeaders;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static pl.newsler.testcommons.TestUserUtils.domain;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.username;

@SuppressWarnings({"java:S5778"})
class JWTFilterTest {
    private final MockUserRepository userRepository = new MockUserRepository();
    private final MockNLIKeyProvider keyProvider = new MockNLIKeyProvider();
    private final MockNLPasswordEncoder passwordEncoder = new MockNLPasswordEncoder();
    private final JWTConfiguration configuration = new JWTConfiguration(userRepository, passwordEncoder, keyProvider);
    private final JWTUtility utility = configuration.jwtUtility();
    private final IJWTAuthService service = configuration.authService(utility);
    private final UserFactory factory = new UserFactory();
    private final JWTVerifier verifier = JWT.require(utility.hmac384()).build();

    private final JWTFilter filter = new JWTFilter(new MockAuthenticationManager(), userRepository, utility);

    @BeforeEach
    void beforeEach() {
        NLId standardId = NLId.of(UUID.randomUUID());
        factory.standard().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.standard_plainPassword())));
        factory.standard().setId(standardId);
        userRepository.save(factory.standard());

        NLId dashedId = NLId.of(UUID.randomUUID());
        factory.dashed().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dashed_plainPassword())));
        factory.dashed().setId(dashedId);
        userRepository.save(factory.dashed());

        NLId dottedId = NLId.of(UUID.randomUUID());
        factory.dotted().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dotted_plainPassword())));
        factory.dotted().setId(dottedId);
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
                passwordEncoder.encrypt(standardUser.getEmail().getValue()),
                passwordEncoder.encrypt(factory.standard_plainPassword())
        );
        String token = service.generateJWT(model);
        DecodedJWT decodedJWT = verify(token);
        JWTResolver.resolveJWT(decodedJWT);

        Assertions.assertEquals(String.valueOf(JWTClaim.JWT_ID), decodedJWT.getId());
        Assertions.assertEquals(JWTClaim.ISSUER, decodedJWT.getIssuer());
        Assertions.assertEquals(standardUser.getEmail().getValue(), decodedJWT.getClaim(JWTClaim.EMAIL).asString());
        Assertions.assertEquals(standardUser.getFirstName().getValue(), decodedJWT.getClaim(JWTClaim.NAME).asString());
        Assertions.assertEquals(standardUser.getRole().toString(), decodedJWT.getClaim(JWTClaim.ROLE).asString());
    }

    @Test
    void shouldDoFilterInternal() {
        NLUser standardUser = factory.standard();
        UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt(standardUser.getEmail().getValue()),
                passwordEncoder.encrypt(factory.standard_plainPassword())
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
                passwordEncoder.encrypt(standardUser.getEmail().getValue()),
                passwordEncoder.encrypt(factory.standard_plainPassword())
        );
        String token = service.generateJWT(model);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        Assertions.assertDoesNotThrow(() -> filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "", "Bearer ", "     ", "Bearer      "})
    void shouldNotDoFilterInternal_BlankTokens(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, token);
        Assertions.assertThrows(UnauthorizedException.class, () -> filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain()));
    }

    @Test
    void shouldNotDoFilterInternal_EmailDoesNotMatchRegExp() {
        String token = generateToken(JWTClaim.ISSUER, "test.d'amaro@.com.dev", firstName(), "USER");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, token);
        Assertions.assertThrows(UnauthorizedException.class, () -> filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain()));
    }

    @Test
    void shouldNotDoFilterInternal_UserNotExists() {
        String token = generateToken(JWTClaim.ISSUER, String.format("%s@%s.dev", username(), domain()), firstName(), "USER");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, token);
        Assertions.assertThrows(UnauthorizedException.class, () -> filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain()));
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
        Assertions.assertThrows(UnauthorizedException.class, () -> filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain()));
    }

    private DecodedJWT verify(String token) {
        return verifier.verify(token);
    }

    private Algorithm hmac384() {
        return Algorithm.HMAC384(keyProvider.getKey(NLPublicAlias.PE_PASSWORD));
    }

    private String generateToken(String issuer, String email, String name, String role) {
        final Instant now = Instant.now();
        return JWT.create()
                .withJWTId(String.valueOf(JWTClaim.JWT_ID))
                .withKeyId(hmac384().getSigningKeyId())
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(60L, ChronoUnit.MINUTES))
                .withClaim(JWTClaim.EMAIL, email)
                .withClaim(JWTClaim.NAME, name)
                .withClaim(JWTClaim.ROLE, role)
                .sign(hmac384());
    }
}
