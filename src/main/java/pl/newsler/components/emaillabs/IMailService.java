package pl.newsler.components.emaillabs;

import pl.newsler.commons.models.NLUuid;
import pl.newsler.components.emaillabs.dto.GetMailStatus;
import pl.newsler.components.emaillabs.dto.MailSendRequest;

import java.util.List;

public interface IMailService {
    void queue(MailSendRequest request);

    List<NLUserMail> fetchAllMails(NLUuid userId);

    GetMailStatus getMailStatus(NLUuid mailId, NLUuid userId);
}
