package pl.newsler.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.signup.dto.UserResendTokenRequest;
import pl.newsler.components.user.ValueProvider;

@CrossOrigin(origins = "*")
@RequestMapping(NLApi.V1 + "/auth/sign-up")
public interface IUserSignupController {
    @PostMapping
    ResponseEntity<ValueProvider> signup(@RequestBody UserCreateRequest request);

    @GetMapping("/confirm")
    ResponseEntity<ValueProvider> confirm(@RequestParam("token") String token);

    @PostMapping("/reconfirm")
    ResponseEntity<ValueProvider> resendToken(@RequestBody UserResendTokenRequest request);
}
