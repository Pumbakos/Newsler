package pl.newsler.commons.utillity;

import lombok.RequiredArgsConstructor;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserGetRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;
import pl.newsler.security.NLIPasswordEncoder;

@RequiredArgsConstructor
public class ObjectDecryptor {
    private final NLIPasswordEncoder encoder;

    public UserGetRequest decrypt(UserGetRequest request) {
        return new UserGetRequest(encoder.decrypt(request.password()), encoder.decrypt(request.email()));
    }

    public UserUpdateRequest decrypt(UserUpdateRequest request) {
        return new UserUpdateRequest(encoder.decrypt(request.appKey()), encoder.decrypt(request.secretKey()), encoder.decrypt(request.smtpAccount()), encoder.decrypt(request.email()));
    }

    public UserDeleteRequest decrypt(UserDeleteRequest request) {
        return new UserDeleteRequest(encoder.decrypt(request.id()), encoder.decrypt(request.password()));
    }

    public UserCreateRequest decrypt(UserCreateRequest request) {
        return new UserCreateRequest(encoder.decrypt(request.name()), encoder.decrypt(request.lastName()), encoder.decrypt(request.email()), encoder.decrypt(request.password()));
    }
}
