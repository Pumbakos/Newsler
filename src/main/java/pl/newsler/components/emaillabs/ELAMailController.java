package pl.newsler.components.emaillabs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.IELAMailController;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.usecase.ELAGetMailResponse;
import pl.newsler.components.emaillabs.usecase.ELAMailSendRequest;
import pl.newsler.commons.exception.InvalidUserDataException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ELAMailController implements IELAMailController {
    private final IELAMailService service;

    @PostMapping
    @Override
    public ResponseEntity<HttpStatus> queue(@RequestBody ELAMailSendRequest request) throws InvalidUserDataException {
        service.queue(request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/{userId}")
    @Override
    public ResponseEntity<List<ELAGetMailResponse>> fetchAllMails(@PathVariable("userId") String userId) throws InvalidUserDataException {
        return ResponseEntity.ok(service.fetchAllMails(NLUuid.of(userId)));
    }
}
