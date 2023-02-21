package pl.newsler.components.receiver;

import pl.newsler.commons.exception.InvalidReceiverDataException;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.receiver.usecase.ReceiverGetResponse;
import pl.newsler.components.receiver.usecase.ReceiverCreateRequest;

import java.util.List;

public interface IReceiverService {
    String addReceiver(ReceiverCreateRequest request, boolean autoSaved) throws InvalidReceiverDataException;
    void autoSaveNewReceiver(List<String> receivers, NLUuid uuid);
    List<ReceiverGetResponse> fetchAllUserReceivers(String userUuid) throws InvalidReceiverDataException;
}
