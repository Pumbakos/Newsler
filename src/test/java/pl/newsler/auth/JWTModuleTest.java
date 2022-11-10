package pl.newsler.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.newsler.api.exceptions.UnauthorizedException;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.components.user.MockUserRepository;
import pl.newsler.components.user.UserDataNotFineException;
import pl.newsler.components.user.UserFactory;
import pl.newsler.security.AlgorithmType;
import pl.newsler.security.MockNLIKeyProvider;
import pl.newsler.security.MockNLPasswordEncoder;
import pl.newsler.security.filters.JWTTestResolver;

import java.util.UUID;

class JWTModuleTest {
    private final MockUserRepository userRepository = new MockUserRepository();
    private final MockNLIKeyProvider keyProvider = new MockNLIKeyProvider();
    private final MockNLPasswordEncoder passwordEncoder = new MockNLPasswordEncoder();
    private final JWTConfiguration configuration = new JWTConfiguration(userRepository, passwordEncoder, keyProvider);
    private final JWTUtility utility = configuration.jwtUtility();
    private final IJWTAuthService service = configuration.authService(utility);
    private final UserFactory factory = new UserFactory();
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
    void shouldGenerateJWT() {
        UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt(factory.standard().getEmail().getValue(), AlgorithmType.AES),
                passwordEncoder.encrypt(factory.standard_plainPassword(), AlgorithmType.AES)
        );
        String token = service.generateJWT(model);

        Assertions.assertTrue(JWTTestResolver.resolve(verify(token)));
    }

    @Test
    void shouldNotGenerateToken_InvalidEmailEncryption_ThrowIllegalArgumentException() {
        UserAuthModel model = new UserAuthModel(
                factory.standard().getEmail().getValue(),
                factory.standard_plainPassword()
        );
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.generateJWT(model));
    }

    @Test
    void shouldNotGenerateToken_InvalidEmail_ThrowUnauthorizedException() {
        UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt("email@app.co", AlgorithmType.AES),
                passwordEncoder.encrypt(factory.dotted_plainPassword(), AlgorithmType.AES)
        );
        Assertions.assertThrows(UnauthorizedException.class, () -> service.generateJWT(model));
    }

    @Test
    void shouldNotGenerateToken_ValidEmail_InvalidPassword_ThrowUnauthorizedException() {
        UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt(factory.standard().getEmail().getValue(), AlgorithmType.AES),
                passwordEncoder.encrypt(factory.dotted_plainPassword(), AlgorithmType.AES)
        );
        Assertions.assertThrows(UnauthorizedException.class, () -> service.generateJWT(model));
    }

    @Test
    void shouldNotGenerateToken_ValidEmail_ValidPassword_AccountLocked_ThrowUnauthorizedException() {
        factory.dotted().setLocked(true);
        factory.dotted().setEnabled(false);
        userRepository.save(factory.dotted());

        UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt(factory.dotted().getEmail().getValue(), AlgorithmType.AES),
                passwordEncoder.encrypt(factory.dotted_plainPassword(), AlgorithmType.AES)
        );
        Assertions.assertThrows(UnauthorizedException.class, () -> service.generateJWT(model));
    }

    private DecodedJWT verify(String token) {
        return verifier.verify(token);
    }
}
