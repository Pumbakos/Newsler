package pl.newsler.components.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;

import static pl.newsler.components.user.UserTestUtils.domain;
import static pl.newsler.components.user.UserTestUtils.firstName;
import static pl.newsler.components.user.UserTestUtils.lastName;
import static pl.newsler.components.user.UserTestUtils.username;

class UserFactory {
    private NLUser standard;
    private NLUser dashed;
    private NLUser dotted;

    UserFactory() {
        createUserDottedEmail();
        createUserDashedEmail();
        createUserStandardEmail();
    }

    NLUser standard() {
        return standard;
    }

    NLUser dashed() {
        return dashed;
    }

    NLUser dotted() {
        return dotted;
    }

    private void createUserDottedEmail() {
        final NLUser user = new NLUser();
        user.setFirstName(NLFirstName.of(firstName()));
        user.setLastName(NLLastName.of(lastName()));
        user.setEmail(NLEmail.of(String.format("%s@%s.dev", username(), username())));
        user.setPassword(NLPassword.of("Pa$$word7hat^match3$"));
        dotted = user;
    }

    private void createUserDashedEmail() {
        final NLUser user = new NLUser();
        user.setFirstName(NLFirstName.of(firstName()));
        user.setLastName(NLLastName.of(lastName()));
        user.setEmail(NLEmail.of(String.format("%s-%s@%s.dev", firstName(), lastName(), domain())));
        user.setPassword(NLPassword.of("UJk6ds81#@^dsa"));
        dashed = user;
    }

    private void createUserStandardEmail() {
        final NLUser user = new NLUser();
        user.setFirstName(NLFirstName.of(firstName()));
        user.setLastName(NLLastName.of(lastName()));
        user.setEmail(NLEmail.of(String.format("%s@%s.dev", firstName(), domain())));
        user.setPassword(NLPassword.of("U$Ad3na923mas$dmi"));
        standard = user;
    }
}
