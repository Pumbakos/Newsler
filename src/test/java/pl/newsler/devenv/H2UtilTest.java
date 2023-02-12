package pl.newsler.devenv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.components.emaillabs.dto.ELAMailSendRequest;

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
        ELAMailSendRequest request = H2Util.createMailSendRequest(H2Util.fullEmail());

        Assertions.assertNotNull(request);
        Assertions.assertNotNull(request.to());
        Assertions.assertFalse(request.to().isEmpty());
        Assertions.assertNotNull(request.cc());
        Assertions.assertFalse(request.cc().isEmpty());
        Assertions.assertNotNull(request.bcc());
        Assertions.assertFalse(request.bcc().isEmpty());
    }
}