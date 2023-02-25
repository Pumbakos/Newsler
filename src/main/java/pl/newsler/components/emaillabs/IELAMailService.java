package pl.newsler.components.emaillabs;

import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.exception.InvalidDateException;
import pl.newsler.components.emaillabs.usecase.ELAGetMailResponse;
import pl.newsler.components.emaillabs.usecase.ELAInstantMailRequest;
import pl.newsler.components.emaillabs.usecase.ELAScheduleMailRequest;

import java.util.List;

public interface IELAMailService {
    void queue(ELAInstantMailRequest request) throws InvalidUserDataException;

    void schedule(ELAScheduleMailRequest request) throws InvalidUserDataException, InvalidDateException;

    List<ELAGetMailResponse> fetchAllMails(NLUuid userId) throws InvalidUserDataException;
}
