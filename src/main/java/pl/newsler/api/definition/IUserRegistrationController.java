package pl.newsler.api.definition;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pl.newsler.models.user.UserRequest;
import pl.newsler.models.user.registration.UserActivationRequest;
import pl.newsler.models.user.registration.ValueProvider;

public interface IUserRegistrationController {
    @PostMapping
    ResponseEntity<ValueProvider> register(@RequestBody UserRequest request);

    @GetMapping(path = "/confirm")
    ResponseEntity<ValueProvider> confirm(@RequestParam("token") String token);

    @PostMapping("/reconfirm")
    ResponseEntity<ValueProvider> resendToken(@RequestBody UserActivationRequest request);
}
