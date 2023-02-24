package pl.newsler.devenv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLSecretKey;
import pl.newsler.commons.model.NLSmtpAccount;
import pl.newsler.components.emaillabs.usecase.ELAInstantMailRequest;

class H2UtilTest {
    @Test
    void shouldCreate_lastNameAndValidate() {
        String value = H2Util.lastName();
        Assertions.assertTrue(NLLastName.of(value).validate());
    }

    @Test
    void shouldCreate_firstNameAndValidate() {
        String value = H2Util.firstName();
        Assertions.assertTrue(NLFirstName.of(value).validate());
    }

    @Test
    void shouldCreate_fullEmailAndValidate() {
        String value = H2Util.fullEmail();
        Assertions.assertTrue(NLEmail.of(value).validate());
    }

    @Test
    void shouldCreate_secretOrAppKeyAndValidate() {
        String value = H2Util.secretOrAppKey();
        Assertions.assertTrue(NLSecretKey.of(value).validate());
    }

    @Test
    void shouldCreate_smtpAccountAndValidate() {
        String value = H2Util.smtpAccount();
        Assertions.assertTrue(NLSmtpAccount.of(value).validate());
    }

    @RepeatedTest(10)
    void shouldCreateMailSendRequest() {
        ELAInstantMailRequest request = H2Util.createMailSendRequest(H2Util.fullEmail());

        Assertions.assertNotNull(request);
        Assertions.assertNotNull(request.to());
        Assertions.assertFalse(request.to().isEmpty());
    }
}