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
import pl.newsler.commons.exceptions.NLException;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLIdType;
import pl.newsler.components.emaillabs.dto.GetMailStatus;
import pl.newsler.components.emaillabs.dto.MailSendRequest;
import pl.newsler.api.exceptions.UserDataNotFineException;

import java.util.List;

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
    public ResponseEntity<List<NLUserMail>> fetchAllMails(@PathVariable("userId") String userId) throws NLException {
        return ResponseEntity.ok(service.fetchAllMails(NLUuid.of(userId)));
    }

    @GetMapping("/{mailId}/user/{userId}")
    @Override
    public ResponseEntity<GetMailStatus> getMailStatus(@PathVariable("mailId") String mailId, @PathVariable("userId") String userId) throws UserDataNotFineException {
        NLUuid mailID = NLUuid.fromStringifyNLId(mailId, NLIdType.MAIL);
        NLUuid userID = NLUuid.of(userId);
        if (!mailID.validate()) {
            throw new UserDataNotFineException("Mail ID invalid.");
        }

        return ResponseEntity.ok(service.getMailStatus(mailID, userID));
    }
}
