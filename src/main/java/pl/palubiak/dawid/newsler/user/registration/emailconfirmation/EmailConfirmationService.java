package pl.palubiak.dawid.newsler.user.registration.emailconfirmation;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailConfirmationService implements EmailConfirmationSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConfirmationService.class);
    private final JavaMailSender mailSender;
    private static final String MSG = "Error while sending email";

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Newsler - Confirm your email");
            helper.setFrom("info@newsler.io");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error(MSG, e);
            throw new IllegalStateException(MSG);
        }
    }
}
