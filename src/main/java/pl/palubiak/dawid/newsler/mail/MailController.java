package pl.palubiak.dawid.newsler.mail;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.palubiak.dawid.newsler.mail.model.MailRequest;
import pl.palubiak.dawid.newsler.mail.pznu.MailSenderService;

import javax.mail.MessagingException;

import static pl.palubiak.dawid.newsler.mail.pznu.MailSenderService.isArrayEmpty;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/v1/api/mails")
@AllArgsConstructor
public class MailController {
    private final MailSenderService mailSenderService;

    @PostMapping
    public ResponseEntity<String> sendMail(@RequestBody MailRequest request) {
        try {
//            final boolean cc = isArrayEmpty(request.getCc());
//            final boolean bcc = isArrayEmpty(request.getBcc());

            mailSenderService.send(request, !isArrayEmpty(request.getCc()), !isArrayEmpty(request.getBcc()));
            return new ResponseEntity<>("Mail is being proceed", HttpStatus.ACCEPTED);
        } catch (MessagingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
