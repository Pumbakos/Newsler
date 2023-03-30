package pl.newsler.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import pl.newsler.api.IJWTAuthController;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.exception.UnauthorizedException;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.StubUserRepository;
import pl.newsler.components.user.TestUserFactory;
import pl.newsler.security.NLAuthenticationToken;
import pl.newsler.security.NLCredentials;
import pl.newsler.security.NLPrincipal;
import pl.newsler.security.StubNLPasswordEncoder;
import pl.newsler.security.filter.JWTTestResolver;
import pl.newsler.testcommons.TestUserUtils;
import pl.newsler.testcommons.environment.KeyStorePropsStrategy;
import pl.newsler.testcommons.environment.StubEnvironment;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

class AuthenticationModuleTest {
    private final StubUserRepository userRepository = new StubUserRepository();
    private final StubNLPasswordEncoder passwordEncoder = new StubNLPasswordEncoder();
    private final Environment env = new StubEnvironment(new KeyStorePropsStrategy());
    private final JWTConfiguration configuration = new JWTConfiguration(userRepository, passwordEncoder, env);
    private final JWTUtility utility = configuration.jwtUtility(
            configuration.jwtValidationKey(configuration.keyStore()),
            configuration.jwtSigningKey(configuration.keyStore())
    );
    private final IJWTAuthService service = configuration.jwtAuthService(utility, configuration.authUserDetailService());
    private final UserDetailsService userDetailsService = configuration.authUserDetailService();
    private final IJWTAuthController controller = new JWTAuthController(service);
    private final TestUserFactory factory = new TestUserFactory();
    private final AuthenticationProvider provider = new NLAuthenticationProvider(userRepository);
    private final JWTVerifier verifier = JWT.require(utility.rsa384()).build();

    @BeforeEach
    void beforeEach() {
        final NLUuid standardId = NLUuid.of(UUID.randomUUID());
        factory.standard().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.standard_plainPassword())));
        factory.standard().setUuid(standardId);
        userRepository.save(factory.standard());

        final NLUuid dashedId = NLUuid.of(UUID.randomUUID());
        factory.dashed().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dashed_plainPassword())));
        factory.dashed().setUuid(dashedId);
        userRepository.save(factory.dashed());

        final NLUuid dottedId = NLUuid.of(UUID.randomUUID());
        factory.dotted().setPassword(NLPassword.of(passwordEncoder.bCrypt().encode(factory.dotted_plainPassword())));
        factory.dotted().setUuid(dottedId);
        userRepository.save(factory.dotted());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturn_200OKWhenTokenGenerated() {
        final NLUser standardUser = factory.standard();
        final UserAuthModel model = new UserAuthModel(
                standardUser.getEmail().getValue(),
                factory.standard_plainPassword()
        );
        final ResponseEntity<String> response = controller.generateJWT(model);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldGenerateJWT() {
        final NLUser standardUser = factory.standard();
        final UserAuthModel model = new UserAuthModel(
                standardUser.getEmail().getValue(),
                factory.standard_plainPassword()
        );
        final String token = service.generateJWT(model);
        Assertions.assertTrue(JWTTestResolver.resolve(verify(token), standardUser));
    }

    @Test
    void shouldNotGenerateToken_InvalidEmail_RegexDoesNotMatch_ThrowUnauthorizedException() {
        final UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt("email'app@app.co"),
                passwordEncoder.encrypt(factory.dotted_plainPassword())
        );
        Assertions.assertThrows(UnauthorizedException.class, () -> service.generateJWT(model));
    }

    @Test
    void shouldNotGenerateToken_InvalidEmail_ThrowUnauthorizedException() {
        final UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt("email@app.co"),
                passwordEncoder.encrypt(factory.dotted_plainPassword())
        );
        Assertions.assertThrows(UnauthorizedException.class, () -> service.generateJWT(model));
    }

    @Test
    void shouldNotGenerateToken_ValidEmail_InvalidPassword_ThrowUnauthorizedException() {
        final UserAuthModel model = new UserAuthModel(
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

        final UserAuthModel model = new UserAuthModel(
                passwordEncoder.encrypt(factory.dotted().getEmail().getValue()),
                passwordEncoder.encrypt(factory.dotted_plainPassword())
        );
        Assertions.assertThrows(UnauthorizedException.class, () -> service.generateJWT(model));
    }

    /* ----------------- LOAD USER ----------------- */
    @Test
    void shouldLoadUserByUsername_ValidEmail_ExistingUser() {
        final NLUser standardUser = factory.standard();
        Assertions.assertDoesNotThrow(() -> userDetailsService.loadUserByUsername(standardUser.getEmail().getValue()));

        final Optional<NLUser> optionalNLUser = userRepository.findById(standardUser.map().getUuid());
        if (optionalNLUser.isEmpty()) {
            Assertions.fail();
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(standardUser.getEmail().getValue());
        Assertions.assertEquals(userDetails.getUsername(), standardUser.getUsername());
        Assertions.assertEquals(userDetails.getPassword(), optionalNLUser.get().getPassword());
    }

    @Test
    void shouldNotLoadUserByUsername_InvalidEmail() {
        Assertions.assertThrows(InvalidUserDataException.class, () -> userDetailsService.loadUserByUsername("user.d'amora@person.dev"));
    }

    @Test
    void shouldNotLoadUserByUsername_ValidEmail_NonExistingUser() {
        Assertions.assertThrows(InvalidUserDataException.class, () -> userDetailsService.loadUserByUsername("user.d-amora@person.dev"));
    }

    @Test
    void shouldAuthenticateToken() {
        final NLUser standardUser = factory.standard();
        Assertions.assertDoesNotThrow(() -> userDetailsService.loadUserByUsername(standardUser.getEmail().getValue()));

        final Optional<NLUser> optionalNLUser = userRepository.findById(standardUser.map().getUuid());
        if (optionalNLUser.isEmpty()) {
            Assertions.fail();
        }

        final NLPrincipal principal = new NLPrincipal(standardUser.map().getUuid(), standardUser.getEmail(), standardUser.getFirstName());
        final NLCredentials credentials = new NLCredentials(standardUser.getNLPassword());
        final Set<SimpleGrantedAuthority> roles = Collections.singleton(new SimpleGrantedAuthority(standardUser.getRole().name()));
        final Authentication authenticationToken = new NLAuthenticationToken(principal, credentials, roles);
        final Authentication authenticate = provider.authenticate(authenticationToken);
        Assertions.assertTrue(authenticate.isAuthenticated());

        final NLAuthenticationToken token = (NLAuthenticationToken) authenticate;
        Assertions.assertTrue(token.isValidated());
    }

    @Test
    void shouldAuthenticateTokenWhenAlreadyValidated() {
        final NLUser standardUser = factory.standard();
        Assertions.assertDoesNotThrow(() -> userDetailsService.loadUserByUsername(standardUser.getEmail().getValue()));

        final Optional<NLUser> optionalNLUser = userRepository.findById(standardUser.map().getUuid());
        if (optionalNLUser.isEmpty()) {
            Assertions.fail();
        }

        final NLPrincipal principal = new NLPrincipal(standardUser.map().getUuid(), standardUser.getEmail(), standardUser.getFirstName());
        final NLCredentials credentials = new NLCredentials(standardUser.getNLPassword());
        final Set<SimpleGrantedAuthority> roles = Collections.singleton(new SimpleGrantedAuthority(standardUser.getRole().name()));
        final Authentication authenticationToken = new NLAuthenticationToken(principal, credentials, roles);
        final Authentication authenticate = provider.authenticate(authenticationToken);
        Assertions.assertTrue(authenticate.isAuthenticated());

        final NLAuthenticationToken token = (NLAuthenticationToken) authenticate;
        Assertions.assertTrue(token.isValidated());

        final NLAuthenticationToken alreadyValidatedToken = (NLAuthenticationToken) provider.authenticate(authenticationToken);
        Assertions.assertTrue(alreadyValidatedToken.isValidated());
    }

    @Test
    void shouldNotAuthenticateTokenAndThrowBadCredentialsException() {
        final NLPrincipal principal = new NLPrincipal(NLUuid.of(UUID.randomUUID()), NLEmail.of(TestUserUtils.email()), NLFirstName.of(TestUserUtils.firstName()));
        final NLCredentials credentials = new NLCredentials(NLPassword.of("Pa$$word7hat^match3$"));
        final Set<SimpleGrantedAuthority> roles = Collections.singleton(new SimpleGrantedAuthority("USER"));
        final Authentication authenticationToken = new NLAuthenticationToken(principal, credentials, roles);
        Assertions.assertThrows(BadCredentialsException.class, () -> provider.authenticate(authenticationToken));
    }

    @Test
    void shouldSupportOnlyNLAuthenticationToken() {
        Assertions.assertTrue(provider.supports(NLAuthenticationToken.class));
        Assertions.assertFalse(provider.supports(AuthenticationProvider.class));
    }

    private DecodedJWT verify(String token) {
        return verifier.verify(token);
    }
}
