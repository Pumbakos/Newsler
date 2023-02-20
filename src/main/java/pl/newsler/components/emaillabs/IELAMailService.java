package pl.newsler.components.emaillabs;

import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.usecase.ELAGetMailResponse;
import pl.newsler.components.emaillabs.usecase.ELAMailSendRequest;

import java.util.List;

public interface IELAMailService {
    void queue(ELAMailSendRequest request) throws InvalidUserDataException;

    List<ELAGetMailResponse> fetchAllMails(NLUuid userId) throws InvalidUserDataException;
}
