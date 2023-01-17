package pl.newsler.components.signup;

import pl.newsler.commons.models.NLToken;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.signup.dto.UserResendTokenRequest;
import pl.newsler.components.user.ValueProvider;

public interface IUserSignupService {

    ValueProvider singUp(UserCreateRequest request);

    ValueProvider confirmToken(NLToken token);

    ValueProvider resendConfirmationToken(UserResendTokenRequest request);
}
