package pl.newsler.components.receiver;

import pl.newsler.commons.exception.InvalidReceiverDataException;
import pl.newsler.components.receiver.usecase.ReceiverGetResponse;
import pl.newsler.components.receiver.usecase.ReceiverCreateRequest;

import java.util.List;

public interface IReceiverService {
    String addReceiver(ReceiverCreateRequest request, boolean autoSaved) throws InvalidReceiverDataException;
    List<ReceiverGetResponse> fetchAllUserReceivers(String userUuid) throws InvalidReceiverDataException;
}
