package pl.newsler.testcommons;

import pl.newsler.auth.UserAuthModel;
import pl.newsler.components.signup.dto.UserCreateRequest;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserGetRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;
import pl.newsler.security.NLIPasswordEncoder;

public class ObjectEncoder {
    private final NLIPasswordEncoder encoder;

    public ObjectEncoder(final NLIPasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public UserGetRequest encrypt(UserGetRequest request) {
        return new UserGetRequest(encoder.encrypt(request.email()), encoder.encrypt(request.password()));
    }

    public UserUpdateRequest encrypt(UserUpdateRequest request) {
        return new UserUpdateRequest(encoder.encrypt(request.email()), encoder.encrypt(request.appKey()), encoder.encrypt(request.secretKey()), encoder.encrypt(request.smtpAccount()));
    }

    public UserDeleteRequest encrypt(UserDeleteRequest request) {
        return new UserDeleteRequest(encoder.encrypt(request.id()), encoder.encrypt(request.password()));
    }

    public UserCreateRequest encrypt(UserCreateRequest request) {
        return new UserCreateRequest(encoder.encrypt(request.name()), encoder.encrypt(request.lastName()), encoder.encrypt(request.email()), encoder.encrypt(request.password()));
    }

    public UserAuthModel encrypt(UserAuthModel model) {
        return new UserAuthModel(encoder.encrypt(model.email()), encoder.encrypt(model.password()));
    }
}
