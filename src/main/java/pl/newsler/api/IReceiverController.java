package pl.newsler.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.newsler.components.receiver.usecase.ReceiverGetResponse;
import pl.newsler.components.receiver.usecase.ReceiverCreateRequest;

import java.util.List;

@RequestMapping(NLApi.V1 + "/api/receivers")
public interface IReceiverController {
    @GetMapping("/{userUuid}")
    ResponseEntity<List<ReceiverGetResponse>> fetchAllUserReceivers(@PathVariable("userUuid") String userUuid);

    @PostMapping
    ResponseEntity<String> addReceiver(@RequestBody ReceiverCreateRequest request);
}
