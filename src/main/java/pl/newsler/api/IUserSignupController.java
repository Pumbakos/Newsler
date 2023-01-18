package pl.newsler.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.newsler.commons.exception.EmailAlreadyConfirmedException;
import pl.newsler.commons.exception.InvalidTokenException;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.exception.TokenExpiredException;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.signup.dto.UserResendTokenRequest;

@CrossOrigin(origins = "*")
@RequestMapping(NLApi.V1 + "/auth/sign-up")
public interface IUserSignupController {
    @PostMapping
    ResponseEntity<String> signup(@RequestBody UserCreateRequest request) throws InvalidUserDataException;

    @GetMapping("/confirm")
    ResponseEntity<String> confirm(@RequestParam("token") String token) throws InvalidTokenException, EmailAlreadyConfirmedException, TokenExpiredException;

    @PostMapping("/reconfirm")
    ResponseEntity<String> resendToken(@RequestBody UserResendTokenRequest request) throws InvalidUserDataException;
}
