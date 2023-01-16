package pl.newsler.components.emaillabs;

import pl.newsler.commons.models.NLId;
import pl.newsler.components.emaillabs.dto.GetMailStatus;
import pl.newsler.components.emaillabs.dto.MailSendRequest;

import java.util.List;

public interface IMailService {
    void queue(MailSendRequest request);

    List<NLUserMail> fetchAllMails(NLId userId);

    GetMailStatus getMailStatus(NLId mailId, NLId userId);
}
