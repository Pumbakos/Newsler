package pl.newsler.api;

import jakarta.ws.rs.QueryParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(NLApi.V1 + "/api/subscription")
public interface ISubscriptionController {
//    @PostMapping
//    ResponseEntity<HttpStatus> subscribe();

    @DeleteMapping("/cancel")
    ResponseEntity<HttpStatus> cancel(@QueryParam("token") final String cancellationToken, @QueryParam("email") final String receiverMail);
}
