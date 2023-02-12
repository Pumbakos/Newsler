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
import pl.newsler.commons.utillity.ObjectUtils;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.signup.dto.UserResendTokenRequest;

@RestController
@RequiredArgsConstructor
class UserSignupController implements IUserSignupController {
    private final IUserSignupService service;

    @Override
    public ResponseEntity<NLStringValue> signup(UserCreateRequest request) throws InvalidUserDataException {
        if (ObjectUtils.isBlank(request)) {
            throw new InvalidUserDataException("Blank or empty data");
        }

        NLStringValue value = service.singUp(request);
        return new ResponseEntity<>(value, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<NLStringValue> confirm(@RequestParam("token") String token) throws InvalidTokenException, EmailAlreadyConfirmedException, TokenExpiredException {
        if (StringUtils.isBlank(token)) {
            throw new InvalidTokenException("Blank or empty data");
        }

        NLStringValue value = service.confirmToken(NLToken.of(token));
        return new ResponseEntity<>(value, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<NLStringValue> resendToken(@RequestBody UserResendTokenRequest request) throws InvalidUserDataException {
        if (ObjectUtils.isBlank(request)) {
            throw new InvalidUserDataException("Blank or empty data");
        }

        NLStringValue value = service.resendConfirmationToken(request);
        return new ResponseEntity<>(value, HttpStatus.OK);
    }
}
