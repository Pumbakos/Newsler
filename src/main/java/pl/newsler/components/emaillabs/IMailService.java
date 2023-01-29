package pl.newsler.components.emaillabs;

import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.components.emaillabs.dto.GetMailStatus;
import pl.newsler.components.emaillabs.dto.MailSendRequest;
import pl.newsler.components.emaillabs.exceptions.ELAMailNotFoundException;

import java.util.List;

public interface IMailService {
    void queue(MailSendRequest request) throws InvalidUserDataException;

    List<NLUserMail> fetchAllMails(NLUuid userId) throws InvalidUserDataException;

    GetMailStatus getMailStatus(NLUuid mailId, NLUuid userId) throws InvalidUserDataException, ELAMailNotFoundException;
}
