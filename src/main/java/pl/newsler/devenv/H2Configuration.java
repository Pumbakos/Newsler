package pl.newsler.devenv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@RequiredArgsConstructor
class H2Configuration {
    private static final Random random = new SecureRandom();
    private final IELAMailRepository mailRepository;
    private final IReceiverRepository receiverRepository;
    private final NLIPasswordEncoder passwordEncoder;
    private final NLIKeyProvider keyProvider;
    @Value("${newsler.security.keystore.app-key-alias}")
    private String appKeyAlias;
    @Value("${newsler.security.keystore.secret-key-alias}")
    private String secretKeyAlias;
    @Value("${newsler.security.keystore.smtp-alias}")
    private String smtpAlias;
    @Value("${newsler.security.keystore.email-alias}")
    private String emailAlias;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp");
    }

    @Bean
    @SuppressWarnings("java:S112")
        //no need to define custom exception
    CommandLineRunner saveUsers(IUserSignupService signupService, IUserRepository userRepository, IConfirmationTokenRepository tokenRepository) {
        final AtomicReference<String> appKeyRef = new AtomicReference<>();
        final AtomicReference<String> secretKeyRef = new AtomicReference<>();
        final AtomicReference<String> smtpRef = new AtomicReference<>();
        final AtomicReference<String> emailRef = new AtomicReference<>();

        return args -> {
            if (keyProvider != null) {
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

                    if (envVariablesNotNull(appKeyRef, secretKeyRef, smtpRef, emailRef)) {
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
                    new UserCreateRequest("Aizholat",
                            "Newsler",
                            emailRef.get(),
                            "Pa$$word7hat^match3$"
                    )
            );

            signupService.singUp(
                    new UserCreateRequest(firstName(),
                            lastName(),
                            String.format("%s@%s.com", username(), domain()),
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

    private boolean envVariablesNotNull(
            AtomicReference<String> appKey,
            AtomicReference<String> secretKey,
            AtomicReference<String> smtp,
            AtomicReference<String> email
    ) {
        return (
                StringUtils.isBlank(appKey.get())
                        || StringUtils.isBlank(secretKey.get())
                        || StringUtils.isBlank(smtp.get())
                        || StringUtils.isBlank(email.get())
        );
    }
}
