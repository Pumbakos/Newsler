package pl.newsler.components.emaillabs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.IMailController;
import pl.newsler.api.exceptions.Advised;
import pl.newsler.commons.models.NLId;
import pl.newsler.components.emaillabs.dto.GetMailStatus;
import pl.newsler.components.emaillabs.dto.MailSendRequest;
import pl.newsler.components.user.UserDataNotFineException;

import java.util.List;

@Advised
@RestController
@RequiredArgsConstructor
public class MailController implements IMailController {
    private final IMailService service;

    @PostMapping
    @Override
    public ResponseEntity<HttpStatus> queue(@RequestBody MailSendRequest request) {
        service.queue(request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/{userId}")
    @Override
    public ResponseEntity<List<NLUserMail>> fetchAllMails(@PathVariable("userId") String userId) {
        NLId id = NLId.of(userId);
        if (!id.validate()) {
            throw new UserDataNotFineException("Invalid ID");
        }

        return ResponseEntity.ok(service.fetchAllMails(id));
    }

    @GetMapping("/{mailId}/user/{userId}")
    @Override
    public ResponseEntity<GetMailStatus> getMail(@PathVariable("mailId") String mailId, @PathVariable("userId") String userId) {
        NLId mailID = NLId.of(mailId);
        NLId userID = NLId.of(userId);
        if (!mailID.validate()) {
            throw new UserDataNotFineException("Mail ID invalid.");
        }
        if (!mailID.validate()) {
            throw new UserDataNotFineException("Mail ID invalid.");
        }

        GetMailStatus mailStatus = service.getMailStatus(mailID, userID);
        return new ResponseEntity<>(mailStatus, HttpStatus.OK);
    }
}
