package pl.newsler.components.emaillabs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.IELAMailController;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.exception.InvalidDateException;
import pl.newsler.components.emaillabs.usecase.ELAGetMailResponse;
import pl.newsler.components.emaillabs.usecase.ELAInstantMailRequest;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.components.emaillabs.usecase.ELAScheduleMailRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ELAMailController implements IELAMailController {
    private final IELAMailService service;

    @Override
    public ResponseEntity<HttpStatus> queueAndExecute(@RequestBody final ELAInstantMailRequest request) throws InvalidUserDataException {
        service.queue(request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<HttpStatus> schedule(@RequestBody final ELAScheduleMailRequest request) throws InvalidUserDataException, InvalidDateException {
        service.schedule(request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<List<ELAGetMailResponse>> fetchAllMails(@PathVariable("userId") final String userId) throws InvalidUserDataException {
        return ResponseEntity.ok(service.fetchAllMails(NLUuid.of(userId)));
    }
}
