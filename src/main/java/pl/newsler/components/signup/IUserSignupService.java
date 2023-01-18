package pl.newsler.components.signup;

import org.jetbrains.annotations.NotNull;
import pl.newsler.commons.exception.EmailAlreadyConfirmedException;
import pl.newsler.commons.exception.InvalidTokenException;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.exception.TokenExpiredException;
import pl.newsler.commons.exception.UserAlreadyExistsException;
import pl.newsler.commons.models.NLStringValue;
import pl.newsler.commons.models.NLToken;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.signup.dto.UserResendTokenRequest;

public interface IUserSignupService {
    @NotNull NLStringValue singUp(UserCreateRequest request) throws InvalidUserDataException, UserAlreadyExistsException;

    @NotNull NLStringValue confirmToken(@NotNull NLToken token) throws InvalidTokenException, EmailAlreadyConfirmedException, TokenExpiredException;

    @NotNull NLStringValue resendConfirmationToken(UserResendTokenRequest request) throws InvalidUserDataException;
}
