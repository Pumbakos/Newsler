package pl.newsler.components.signup;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.IUserSignupController;
import pl.newsler.commons.models.NLToken;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.signup.dto.UserResendTokenRequest;
import pl.newsler.components.user.ValueProvider;

@RestController
@RequiredArgsConstructor
public class UserSignupController implements IUserSignupController {
    private final IUserSignupService signUpService;

    @Override
    public ResponseEntity<ValueProvider> signup(UserCreateRequest request) {
        return new ResponseEntity<>(signUpService.singUp(request), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ValueProvider> resendToken(@RequestBody UserResendTokenRequest request) {
        ValueProvider valueProvider = signUpService.resendConfirmationToken(request);

        return valueProvider == ValueProvider.RESENT ?
                new ResponseEntity<>(ValueProvider.RESENT, HttpStatus.OK) :
                new ResponseEntity<>(ValueProvider.NOT_SENT, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ValueProvider> confirm(@RequestParam("token") String token) {
        ValueProvider valueProvider = signUpService.confirmToken(NLToken.of(token));

        return valueProvider == ValueProvider.CONFIRMED ?
                new ResponseEntity<>(ValueProvider.CONFIRMED, HttpStatus.OK) :
                new ResponseEntity<>(ValueProvider.NOT_CONFIRMED, HttpStatus.BAD_REQUEST);
    }
}
