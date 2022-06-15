package pl.palubiak.dawid.newsler.mail.pznu;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.palubiak.dawid.newsler.mail.model.MailRequest;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class MailSenderService {
    private final JavaMailSender mailSender;

    @Async
    public void send(MailRequest request, boolean cc, boolean bcc) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        helper.setFrom(request.getFrom());
        helper.setTo(request.getTo());
        helper.setSubject(request.getSubject());
        helper.setText(request.getText(), true);

        if (cc) {
            helper.setCc(request.getCc());
        }
        if (bcc) {
            helper.setBcc(request.getBcc());
        }
        mailSender.send(mimeMessage);
    }

    public static boolean isArrayEmpty(String[] mails){
        boolean isEmpty = false;
        if (mails == null || mails[0].equals("")){
            return true;
        }
        for (String mail : mails) {
            if (mail.isEmpty() || mail.isBlank()) {
                isEmpty = true;
            }
        }
        return isEmpty;
    }
}
