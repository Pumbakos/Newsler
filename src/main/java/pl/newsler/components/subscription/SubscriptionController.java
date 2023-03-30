package pl.newsler.components.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.ISubscriptionController;

@RestController
@RequiredArgsConstructor
class SubscriptionController implements ISubscriptionController {
    private final ISubscriptionService service;

    @Override
    public ResponseEntity<HttpStatus> subscribe(final String subscriptionToken, final String receiverMail) {
        service.subscribe(subscriptionToken, receiverMail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> cancel(final String subscriptionToken, final String receiverMail) {
        service.cancel(subscriptionToken, receiverMail);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
