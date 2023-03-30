package pl.newsler.components.signup;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
import pl.newsler.commons.model.NLStringValue;
import pl.newsler.commons.model.NLToken;
import pl.newsler.commons.utillity.ObjectUtils;
import pl.newsler.components.signup.usecase.UserCreateRequest;
import pl.newsler.components.signup.usecase.UserResendTokenRequest;
import pl.newsler.internal.NewslerDesignerServiceProperties;

@RestController
@RequiredArgsConstructor
class UserSignupController implements IUserSignupController {
    private final IUserSignupService service;
    @Value("${newsler.designer.schema}")
    private NewslerDesignerServiceProperties.Schema schema;
    @Value("${newsler.designer.domain-name}")
    private String domainName;
    @Value("${newsler.designer.port}")
    private int port;

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
        String redirectUrl;
        try {
            service.confirmToken(NLToken.of(token));
            redirectUrl = String.format("%s://%s:%d/sign-up/confirmation?token=ok", schema, domainName, port);
        } catch (InvalidTokenException | InvalidUserDataException e) {
            redirectUrl = String.format("%s://%s:%d/sign-up/confirmation?token=invalid", schema, domainName, port);
        } catch (EmailAlreadyConfirmedException e) {
            redirectUrl = String.format("%s://%s:%d/sign-up/confirmation?token=confirmed", schema, domainName, port);
        } catch (TokenExpiredException e) {
            redirectUrl = String.format("%s://%s:%d/sign-up/confirmation?token=expired", schema, domainName, port);
        }
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).header(HttpHeaders.LOCATION, redirectUrl).build();
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
