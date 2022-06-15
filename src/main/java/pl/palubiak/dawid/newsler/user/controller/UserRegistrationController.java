package pl.palubiak.dawid.newsler.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.palubiak.dawid.newsler.user.service.UserRegistrationService;
import pl.palubiak.dawid.newsler.user.registration.ValueProvider;
import pl.palubiak.dawid.newsler.user.model.requestmodel.ActivationRequest;
import pl.palubiak.dawid.newsler.user.model.requestmodel.UserRequest;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users/registration")
public class UserRegistrationController {
    private final UserRegistrationService userRegistrationService;

    @Autowired
    public UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping
    public ResponseEntity<ValueProvider> register(@RequestBody UserRequest request) {
        ValueProvider valueProvider = userRegistrationService.register(request);

        return valueProvider == ValueProvider.REGISTERED ?
                new ResponseEntity<>(ValueProvider.REGISTERED, HttpStatus.ACCEPTED) :
                new ResponseEntity<>(ValueProvider.NOT_REGISTERED, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/reconfirm")
    public ResponseEntity<ValueProvider> resendToken(@RequestBody ActivationRequest request) {
        ValueProvider valueProvider = userRegistrationService.resendConfirmationToken(request);

        return valueProvider == ValueProvider.RESENT ?
                new ResponseEntity<>(ValueProvider.RESENT, HttpStatus.OK) :
                new ResponseEntity<>(ValueProvider.NOT_SENT, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<ValueProvider> confirm(@RequestParam("token") String token) {
        ValueProvider valueProvider = userRegistrationService.confirmToken(token);

        return valueProvider == ValueProvider.CONFIRMED ?
                new ResponseEntity<>(ValueProvider.CONFIRMED, HttpStatus.OK) :
                new ResponseEntity<>(ValueProvider.NOT_CONFIRMED, HttpStatus.BAD_REQUEST);
    }
}
