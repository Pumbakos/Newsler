package pl.newsler.components.emaillabs;

import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.components.emaillabs.dto.GetMailResponse;
import pl.newsler.components.emaillabs.dto.MailSendRequest;

import java.util.List;

public interface IELAMailService {
    void queue(MailSendRequest request) throws InvalidUserDataException;

    List<GetMailResponse> fetchAllMails(NLUuid userId) throws InvalidUserDataException;
}
