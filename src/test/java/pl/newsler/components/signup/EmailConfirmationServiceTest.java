package pl.newsler.components.signup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pl.newsler.commons.exception.EmailCouldNotSentException;

public class EmailConfirmationServiceTest {
    private final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    private final IEmailConfirmationService service = new EmailConfirmationService(mailSender);

    @BeforeEach
    void setUp() {
        mailSender.setUsername("admin");
        mailSender.setPassword("admin");
        mailSender.setHost("localhost");
        mailSender.setPort(1025);
    }

    @Test
    void shouldSendConfirmationEmailWhenValidData() {
        Assertions.assertDoesNotThrow(() -> service.send("valid@email.test", "Hello from tests!"));
    }

    @Test
    void shouldNotSendConfirmationEmailWhenReceiverInvalid() {
        Assertions.assertThrows(EmailCouldNotSentException.class, () -> service.send(null, "Hello from tests!"));
        Assertions.assertThrows(EmailCouldNotSentException.class, () -> service.send("", "Hello from tests!"));
        Assertions.assertThrows(EmailCouldNotSentException.class, () -> service.send(" ", "Hello from tests!"));
        Assertions.assertThrows(EmailCouldNotSentException.class, () -> service.send("invalid@mail", "Hello from tests!"));
        Assertions.assertThrows(EmailCouldNotSentException.class, () -> service.send("a.b@c.d", "Hello from tests!"));
    }
}
