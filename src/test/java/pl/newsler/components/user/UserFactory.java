package pl.newsler.components.user;

import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.commons.models.NLType;

import static pl.newsler.testcommons.TestUserUtils.domain;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.lastName;
import static pl.newsler.testcommons.TestUserUtils.username;

public class UserFactory {
    private NLUser standard;
    private NLUser dashed;
    private NLUser dotted;

    public UserFactory() {
        createUserDottedEmail();
        createUserDashedEmail();
        createUserStandardEmail();
    }

    public NLUser standard() {
        return standard;
    }

    public NLUser dashed() {
        return dashed;
    }

    public NLUser dotted() {
        return dotted;
    }

    public String standard_plainPassword() {
        return "Pa$$word7hat^match3$";
    }

    public String dashed_plainPassword() {
        return "UJk6ds81#@^dsa";
    }

    public String dotted_plainPassword() {
        return "U$Ad3na923mas$dmi";
    }

    private void createUserDottedEmail() {
        final NLUser user = new NLUser();
        user.setFirstName(NLFirstName.of(firstName()));
        user.setLastName(NLLastName.of(lastName()));
        user.setEmail(NLEmail.of(String.format("%s@%s.dev", username(), username())));
        user.setPassword(NLPassword.of("Pa$$word7hat^match3$"));
        user.setEnabled(true);
        user.setLocked(false);
        user.setRole(NLType.USER);
        dotted = user;
    }

    private void createUserDashedEmail() {
        final NLUser user = new NLUser();
        user.setFirstName(NLFirstName.of(firstName()));
        user.setLastName(NLLastName.of(lastName()));
        user.setEmail(NLEmail.of(String.format("%s-%s@%s.dev", firstName(), lastName(), domain())));
        user.setPassword(NLPassword.of("UJk6ds81#@^dsa"));
        user.setEnabled(true);
        user.setLocked(false);
        user.setRole(NLType.USER);
        dashed = user;
    }

    private void createUserStandardEmail() {
        final NLUser user = new NLUser();
        user.setFirstName(NLFirstName.of(firstName()));
        user.setLastName(NLLastName.of(lastName()));
        user.setEmail(NLEmail.of(String.format("%s@%s.dev", firstName(), domain())));
        user.setPassword(NLPassword.of("U$Ad3na923mas$dmi"));
        user.setEnabled(true);
        user.setLocked(false);
        user.setRole(NLType.USER);
        standard = user;
    }
}
