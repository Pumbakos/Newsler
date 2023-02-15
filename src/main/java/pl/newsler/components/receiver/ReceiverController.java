package pl.newsler.components.receiver;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.IReceiverController;
import pl.newsler.components.receiver.dto.ReceiverGetResponse;
import pl.newsler.components.receiver.dto.ReceiverCreateRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReceiverController implements IReceiverController {
    private final IReceiverService service;

    @Override
    public ResponseEntity<String> addReceiver(final ReceiverCreateRequest request) {
        return new ResponseEntity<>(service.addReceiver(request, false), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReceiverGetResponse>> fetchAllUserReceivers(final String userUuid) {
        List<ReceiverGetResponse> responses = service.fetchAllUserReceivers(userUuid);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
