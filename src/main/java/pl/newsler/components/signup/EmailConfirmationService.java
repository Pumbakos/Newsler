package pl.newsler.components.signup;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import pl.newsler.commons.exception.EmailCouldNotSentException;
import pl.newsler.commons.models.NLEmail;

@Slf4j
@RequiredArgsConstructor
class EmailConfirmationService implements IEmailConfirmationService {
    private static final String MSG = "Error while sending email\n {}";
    private final JavaMailSender mailSender;

    @Async
    @Override
    public void send(String to, String text) throws EmailCouldNotSentException {
        if (!NLEmail.of(to).validate()) {
            throw new EmailCouldNotSentException("Invalid recipient");
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setText(text, true);
            helper.setTo(to);
            helper.setSubject("Newsler - Confirm your email");
            helper.setFrom("info@newsler.io");
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error(MSG, e.getMessage());
            throw new EmailCouldNotSentException("Sign-up confirmation message", e.getMessage());
        }
    }
}
