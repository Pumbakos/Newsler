package pl.newsler.devenv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLNickname;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.components.emaillabs.IELAMailRepository;
import pl.newsler.components.emaillabs.ELAMailDetails;
import pl.newsler.components.emaillabs.ELAUserMail;
import pl.newsler.components.receiver.IReceiverRepository;
import pl.newsler.components.receiver.Receiver;
import pl.newsler.components.signup.IConfirmationTokenRepository;
import pl.newsler.components.signup.IUserSignupService;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
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

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp");
    }

    @Bean
    @SuppressWarnings("java:S112")
        //no need to define custom exception
    CommandLineRunner saveUsers(IUserSignupService signupService, IUserRepository userRepository, IConfirmationTokenRepository tokenRepository) {
        final AtomicReference<String> appKey = new AtomicReference<>();
        final AtomicReference<String> secretKey = new AtomicReference<>();
        final AtomicReference<String> smtp = new AtomicReference<>();
        final AtomicReference<String> email = new AtomicReference<>();

        try {
            appKey.set(System.getenv("NEWSLER_APP_KEY"));
            secretKey.set(System.getenv("NEWSLER_SECRET_KEY"));
            smtp.set(System.getenv("NEWSLER_SMTP"));
            email.set(System.getenv("NEWSLER_EMAIL"));

            if (envVariablesNotNull(appKey, secretKey, smtp, email)) {
                throw new Exception();
            }
        } catch (Exception e) {
            appKey.set(secretOrAppKey());
            secretKey.set(secretOrAppKey());
            smtp.set(smtpAccount());
            email.set("newslerowsky@app.co.devenv");
        }

        return args -> {
            signupService.singUp(
                    new UserCreateRequest("Aizholat",
                            "Newsler",
                            email.get(),
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
                user.setEnabled(true);
                saveUserMails(user);
                saveUserReceivers(user);

                if (user.getEmail().getValue().equals(passwordEncoder.encrypt(email.get()))) {
                    user.setAppKey(NLAppKey.of(passwordEncoder.encrypt(appKey.get())));
                    user.setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(secretKey.get())));
                    user.setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(smtp.get())));
                } else {
                    user.setAppKey(NLAppKey.of(passwordEncoder.encrypt(secretOrAppKey())));
                    user.setSecretKey(NLSecretKey.of(passwordEncoder.encrypt(secretOrAppKey())));
                    user.setSmtpAccount(NLSmtpAccount.of(passwordEncoder.encrypt(smtpAccount())));
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
                    user.map().getId(),
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
            mailRepository.save(ELAUserMail.of(user.map().getId(), ELAMailDetails.of(H2Util.createMailSendRequest(user.getEmail().getValue()))));
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
