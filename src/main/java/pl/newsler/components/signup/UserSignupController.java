package pl.newsler.components.signup;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.IUserSignupController;
import pl.newsler.commons.exception.EmailAlreadyConfirmedException;
import pl.newsler.commons.exception.InvalidTokenException;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.exception.TokenExpiredException;
import pl.newsler.commons.models.NLStringValue;
import pl.newsler.commons.models.NLToken;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.signup.dto.UserResendTokenRequest;

@RestController
@RequiredArgsConstructor
class UserSignupController implements IUserSignupController {
    private final IUserSignupService signUpService;

    @Override
    public ResponseEntity<String> signup(UserCreateRequest request) throws InvalidUserDataException {
        NLStringValue value = signUpService.singUp(request);
        return new ResponseEntity<>(value.getValue(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> confirm(@RequestParam("token") String token) throws InvalidTokenException, EmailAlreadyConfirmedException, TokenExpiredException {
        if (StringUtils.isBlank(token)) {
            throw new InvalidTokenException("");
        }

        NLStringValue value = signUpService.confirmToken(NLToken.of(token));
        return new ResponseEntity<>(value.getValue(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> resendToken(@RequestBody UserResendTokenRequest request) throws InvalidUserDataException {
        NLStringValue value = signUpService.resendConfirmationToken(request);
        return new ResponseEntity<>(value.getValue(), HttpStatus.OK);
    }
}
