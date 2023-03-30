package pl.newsler.devenv;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.newsler.commons.model.NLAppKey;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLNickname;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.commons.model.NLStringValue;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.ELAUserMail;
import pl.newsler.components.emaillabs.IELAMailRepository;
import pl.newsler.components.emaillabs.executor.ELAInstantMailDetails;
import pl.newsler.components.receiver.IReceiverRepository;
import pl.newsler.components.receiver.Receiver;
import pl.newsler.components.signup.IConfirmationTokenRepository;
import pl.newsler.components.signup.IUserSignupService;
import pl.newsler.components.signup.usecase.UserCreateRequest;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.internal.PropertiesUtil;
import pl.newsler.security.NLIKeyProvider;
import pl.newsler.security.NLIPasswordEncoder;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static pl.newsler.devenv.H2Util.domain;
import static pl.newsler.devenv.H2Util.firstName;
import static pl.newsler.devenv.H2Util.fullEmail;
import static pl.newsler.devenv.H2Util.lastName;
import static pl.newsler.devenv.H2Util.secretOrAppKey;
import static pl.newsler.devenv.H2Util.smtpAccount;
import static pl.newsler.devenv.H2Util.username;

@Slf4j
@Configuration(proxyBeanMethods = false)
class H2Configuration {
    private static final Random random = new SecureRandom();
    private final IELAMailRepository mailRepository;
    private final IReceiverRepository receiverRepository;
    private final NLIPasswordEncoder passwordEncoder;
    private final NLIKeyProvider keyProvider;
    private final String appKeyAlias;
    private final String secretKeyAlias;
    private final String smtpAlias;
    private final String emailAlias;

    H2Configuration(final IELAMailRepository mailRepository, final IReceiverRepository receiverRepository,
                    final NLIPasswordEncoder passwordEncoder, final NLIKeyProvider keyProvider, final Environment env) {
        this.mailRepository = mailRepository;
        this.receiverRepository = receiverRepository;
        this.passwordEncoder = passwordEncoder;
        this.keyProvider = keyProvider;
        appKeyAlias = env.getProperty("newsler.security.keystore.app-key-alias");
        secretKeyAlias = env.getProperty("newsler.security.keystore.secret-key-alias");
        smtpAlias = env.getProperty("newsler.security.keystore.smtp-alias");
        emailAlias = env.getProperty("newsler.security.keystore.email-alias");
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp");
    }

    @Bean
    @SuppressWarnings("java:S112")
        //no need to define custom exception
    CommandLineRunner saveUsers(final IUserSignupService signupService, final IUserRepository userRepository,
                                final IConfirmationTokenRepository tokenRepository) {
        final AtomicReference<String> appKeyRef = new AtomicReference<>();
        final AtomicReference<String> secretKeyRef = new AtomicReference<>();
        final AtomicReference<String> smtpRef = new AtomicReference<>();
        final AtomicReference<String> emailRef = new AtomicReference<>();

        return args -> {
            if (PropertiesUtil.arePropsSet(appKeyAlias, secretKeyAlias, smtpAlias, emailAlias)) {
                appKeyRef.set(new String(keyProvider.getKey(appKeyAlias)));
                secretKeyRef.set(new String(keyProvider.getKey(secretKeyAlias)));
                smtpRef.set(new String(keyProvider.getKey(smtpAlias)));
                emailRef.set(new String(keyProvider.getKey(emailAlias)));
            } else {
                try {
                    appKeyRef.set(System.getenv("NEWSLER_APP_KEY"));
                    secretKeyRef.set(System.getenv("NEWSLER_SECRET_KEY"));
                    smtpRef.set(System.getenv("NEWSLER_SMTP"));
                    emailRef.set(System.getenv("NEWSLER_EMAIL"));

                    if (!PropertiesUtil.arePropsSet(appKeyRef.get(), secretKeyRef.get(), smtpRef.get(), emailRef.get())) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    appKeyRef.set(secretOrAppKey());
                    secretKeyRef.set(secretOrAppKey());
                    smtpRef.set(smtpAccount());
                    emailRef.set("newslerowsky@app.co.devenv");
                }
            }

            signupService.singUp(
                    new UserCreateRequest(
                            "Aizholat",
                            "Newsler",
                            emailRef.get(),
                            "Pa$$word7hat^match3$"
                    )
            );

            final String testUserMail = "just.test@dev-newsler.pl";
            signupService.singUp(
                    new UserCreateRequest(
                            "TestUser",
                            "JustForTests",
                            testUserMail,
                            "op@Q7#9FtE$%0X^#UZ"
                    )
            );

            tokenRepository.findAll().forEach(token -> token.setConfirmationDate(LocalDateTime.now()));
            final List<NLUser> all = userRepository.findAll();
            for (NLUser user : all) {
                saveUserMails(user);
                saveUserReceivers(user);

                if (user.getEmail().getValue().equals(emailRef.get())) {
                    user.setAppKey(NLAppKey.of(passwordEncoder.encrypt(appKeyRef.get())));
                    user.setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(secretKeyRef.get())));
                    user.setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(smtpRef.get())));
                    user.setDefaultTemplateId(NLStringValue.of("cda1b272"));
                } else {
                    user.setAppKey(NLAppKey.of(passwordEncoder.encrypt(secretOrAppKey())));
                    user.setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(secretOrAppKey())));
                    user.setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(smtpAccount())));
                    user.setDefaultTemplateId(NLStringValue.of("DyFv5w80"));
                }

                userRepository.save(user);
            }

            userRepository.findByEmail(NLEmail.of(testUserMail)).ifPresent(user -> {
                user.setEnabled(true);
            });
        };
    }

    private void saveUserReceivers(final NLUser user) {
        for (int i = 0; i < random.nextInt(14) + 8; i++) {
            receiverRepository.save(new Receiver(
                    NLUuid.of(UUID.randomUUID()),
                    IReceiverRepository.version,
                    user.map().getUuid(),
                    NLEmail.of(fullEmail()),
                    NLNickname.of(firstName()),
                    NLFirstName.of(firstName()),
                    NLLastName.of(lastName()),
                    false
            ));
        }
    }

    private void saveUserMails(final NLUser user) {
        for (int i = 0; i < random.nextInt(9) + 5; i++) {
            mailRepository.save(ELAUserMail.of(user.map().getUuid(), ELAInstantMailDetails.of(H2Util.createMailSendRequest(user.getEmail().getValue()))));
        }
    }
}
