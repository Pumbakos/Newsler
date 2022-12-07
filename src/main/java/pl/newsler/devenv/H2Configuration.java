package pl.newsler.devenv;

import org.h2.tools.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.components.user.IUserCrudService;

import java.sql.SQLException;

import static pl.newsler.devenv.H2Util.domain;
import static pl.newsler.devenv.H2Util.firstName;
import static pl.newsler.devenv.H2Util.lastName;
import static pl.newsler.devenv.H2Util.secretOrAppKey;
import static pl.newsler.devenv.H2Util.smtpAccount;
import static pl.newsler.devenv.H2Util.username;

@Configuration(proxyBeanMethods = false)
class H2Configuration {
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp");
    }

    @Bean
    CommandLineRunner saveUsers(IUserCrudService service) {
        return args -> {
            NLId id1 = service.create(
                    NLFirstName.of("Aizholat"),
                    NLLastName.of("Newsler"),
                    NLEmail.of("newslerowsky@app.co.devenv"),
                    NLPassword.of("Pa$$word7hat^match3$")
            );

            NLId id2 = service.create(
                    NLFirstName.of(firstName()),
                    NLLastName.of(lastName()),
                    NLEmail.of(String.format("%s@%s.com", username(), domain())),
                    NLPassword.of("op@Q7#9FtE$%0X^#UZ")
            );

            NLId id3 = service.create(
                    NLFirstName.of(firstName()),
                    NLLastName.of(lastName()),
                    NLEmail.of(String.format("%s@%s.newsler.pl", username(), domain())),
                    NLPassword.of("^a1u3@tbZ0I0Cd0W")
            );

            NLId id4 = service.create(
                    NLFirstName.of(firstName()),
                    NLLastName.of(lastName()),
                    NLEmail.of(String.format("%s@%s.co", username(), domain())),
                    NLPassword.of("$^P931p)a$*E#7r4)4$$")
            );

            NLId id5 = service.create(
                    NLFirstName.of(firstName()),
                    NLLastName.of(lastName()),
                    NLEmail.of(String.format("%s@%s.ai", username(), domain())),
                    NLPassword.of("E#7r4)4$$$^P931p)a$*")
            );

            service.update(id1, NLAppKey.of(secretOrAppKey()), NLSecretKey.of(secretOrAppKey()), NLSmtpAccount.of(smtpAccount()));
            service.update(id2, NLAppKey.of(secretOrAppKey()), NLSecretKey.of(secretOrAppKey()), NLSmtpAccount.of(smtpAccount()));
            service.update(id3, NLAppKey.of(secretOrAppKey()), NLSecretKey.of(secretOrAppKey()), NLSmtpAccount.of(smtpAccount()));
            service.update(id4, NLAppKey.of(secretOrAppKey()), NLSecretKey.of(secretOrAppKey()), NLSmtpAccount.of(smtpAccount()));
            service.update(id5, NLAppKey.of(secretOrAppKey()), NLSecretKey.of(secretOrAppKey()), NLSmtpAccount.of(smtpAccount()));
        };
    }

}
