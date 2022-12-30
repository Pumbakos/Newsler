package pl.newsler.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import pl.newsler.api.IJWTAuthController;
import pl.newsler.api.exceptions.UnauthorizedException;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.components.user.UserDataNotFineException;
import pl.newsler.security.StubNLIKeyProvider;
import pl.newsler.security.StubNLPasswordEncoder;
import pl.newsler.security.filters.JWTTestResolver;

import java.util.Optional;
import java.util.UUID;

class JWTModuleTest {
    private final StubUserRepository userRepository = new StubUserRepository();
    private final StubNLIKeyProvider keyProvider = new StubNLIKeyProvider();
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final JWTConfiguration configuration = new JWTConfiguration(userRepository, passwordEncoder, keyProvider);
    private final JWTUtility utility = configuration.jwtUtility();
    private final IJWTAuthService service = configuration.jwtAuthService(utility);
    private final IJWTAuthController controller = new JWTAuthController(service);
    private final TestUserFactory factory = new TestUserFactory();
    final JWTVerifier verifier = JWT.require(utility.hmac384()).build();

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
    void shouldReturn_200OK() {
        NLUser standardUser = factory.standard();
        UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt(standardUser.getEmail().getValue()),
                passwordEncoder.encrypt(factory.standard_plainPassword())
        );
        ResponseEntity<String> response = controller.generateJWT(model);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturn_401Unauthorized_InvalidEncryption() {
        UserAuthModel model = new UserAuthModel(
                factory.standard().getEmail().getValue(),
                factory.standard_plainPassword()
        );
        ResponseEntity<String> response = controller.generateJWT(model);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void shouldGenerateJWT() {
        NLUser standardUser = factory.standard();
        UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt(standardUser.getEmail().getValue()),
                passwordEncoder.encrypt(factory.standard_plainPassword())
        );
        String token = service.generateJWT(model);
        Assertions.assertTrue(JWTTestResolver.resolve(verify(token)));
    }

    @Test
    void shouldNotGenerateToken_InvalidEncryption_ThrowIllegalArgumentException() {
        UserAuthModel model = new UserAuthModel(
                factory.standard().getEmail().getValue(),
                factory.standard_plainPassword()
        );
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.generateJWT(model));
    }

    @Test
    void shouldNotGenerateToken_InvalidEmail_RegexDoesNotMatch_ThrowUnauthorizedException() {
        UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt("email'app@app.co"),
                passwordEncoder.encrypt(factory.dotted_plainPassword())
        );
        Assertions.assertThrows(UnauthorizedException.class, () -> service.generateJWT(model));
    }

    @Test
    void shouldNotGenerateToken_InvalidEmail_ThrowUnauthorizedException() {
        UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt("email@app.co"),
                passwordEncoder.encrypt(factory.dotted_plainPassword())
        );
        Assertions.assertThrows(UnauthorizedException.class, () -> service.generateJWT(model));
    }

    @Test
    void shouldNotGenerateToken_ValidEmail_InvalidPassword_ThrowUnauthorizedException() {
        UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt(factory.standard().getEmail().getValue()),
                passwordEncoder.encrypt(factory.dotted_plainPassword())
        );
        Assertions.assertThrows(UnauthorizedException.class, () -> service.generateJWT(model));
    }

    @Test
    void shouldNotGenerateToken_ValidEmail_ValidPassword_AccountLocked_ThrowUnauthorizedException() {
        factory.dotted().setLocked(true);
        factory.dotted().setEnabled(false);
        userRepository.save(factory.dotted());

        UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt(factory.dotted().getEmail().getValue()),
                passwordEncoder.encrypt(factory.dotted_plainPassword())
        );
        Assertions.assertThrows(UnauthorizedException.class, () -> service.generateJWT(model));
    }

    /* ----------------- LOAD USER ----------------- */
    @Test
    void shouldLoadUserByUsername_ValidEmail_ExistingUser() {
        final NLUser standardUser = factory.standard();
        Assertions.assertDoesNotThrow(() -> service.loadUserByUsername(standardUser.getEmail().getValue()));

        final Optional<NLUser> optionalNLUser = userRepository.findById(standardUser.map().getId());
        if (optionalNLUser.isEmpty()) {
            Assertions.fail();
        }

        final UserDetails userDetails = service.loadUserByUsername(standardUser.getEmail().getValue());
        Assertions.assertEquals(userDetails.getUsername(), standardUser.getUsername());
        Assertions.assertEquals(userDetails.getPassword(), optionalNLUser.get().getPassword());
    }

    @Test
    void shouldNotLoadUserByUsername_InvalidEmail() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.loadUserByUsername("user.d'amora@person.dev"));
    }

    @Test
    void shouldNotLoadUserByUsername_ValidEmail_NonExistingUser() {
        Assertions.assertThrows(UserDataNotFineException.class, () -> service.loadUserByUsername("user.d-amora@person.dev"));
    }

    private DecodedJWT verify(String token) {
        return verifier.verify(token);
    }
}
