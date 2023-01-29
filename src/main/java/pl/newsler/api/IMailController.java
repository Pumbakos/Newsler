package pl.newsler.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.components.emaillabs.NLUserMail;
import pl.newsler.components.emaillabs.dto.GetMailStatus;
import pl.newsler.components.emaillabs.dto.MailSendRequest;

import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping(NLApi.V1 + "/api/mails")
public interface IMailController {
    @PostMapping
    ResponseEntity<HttpStatus> queue(@RequestBody MailSendRequest request) throws InvalidUserDataException;

    @GetMapping("/{userId}")
    ResponseEntity<List<NLUserMail>> fetchAllMails(@PathVariable("userId") String userId) throws InvalidUserDataException;

    @GetMapping("/{mailId}/user/{userId}")
    ResponseEntity<GetMailStatus> getMailStatus(@PathVariable("mailId") String mailId, @PathVariable("userId") String userId) throws InvalidUserDataException;
}
