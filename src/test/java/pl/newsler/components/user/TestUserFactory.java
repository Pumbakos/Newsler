package pl.newsler.components.user;

import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.commons.model.NLUserType;

import static pl.newsler.testcommons.TestUserUtils.domain;
import static pl.newsler.testcommons.TestUserUtils.firstName;
import static pl.newsler.testcommons.TestUserUtils.lastName;
import static pl.newsler.testcommons.TestUserUtils.username;

public class TestUserFactory {
    private NLUser standard;
    private NLUser dashed;
    private NLUser dotted;

    public TestUserFactory() {
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

    private void createUserStandardEmail() {
        final NLUser user = new NLUser();
        user.setFirstName(NLFirstName.of(firstName()));
        user.setLastName(NLLastName.of(lastName()));
        user.setEmail(NLEmail.of(String.format("%s@%s.dev", firstName(), domain())));
        user.setPassword(NLPassword.of("Pa$$word7hat^match3$"));
        user.setEnabled(true);
        user.setLocked(false);
        user.setRole(NLUserType.USER);
        standard = user;
    }

    private void createUserDashedEmail() {
        final NLUser user = new NLUser();
        user.setFirstName(NLFirstName.of(firstName()));
        user.setLastName(NLLastName.of(lastName()));
        user.setEmail(NLEmail.of(String.format("%s-%s@%s.dev", firstName(), lastName(), domain())));
        user.setPassword(NLPassword.of("UJk6ds81#@^dsa"));
        user.setEnabled(true);
        user.setLocked(false);
        user.setRole(NLUserType.USER);
        dashed = user;
    }

    private void createUserDottedEmail() {
        final NLUser user = new NLUser();
        user.setFirstName(NLFirstName.of(firstName()));
        user.setLastName(NLLastName.of(lastName()));
        user.setEmail(NLEmail.of(String.format("%s@%s.dev", username(), username())));
        user.setPassword(NLPassword.of("U$Ad3na923mas$dmi"));
        user.setEnabled(true);
        user.setLocked(false);
        user.setRole(NLUserType.USER);
        dotted = user;
    }
}
