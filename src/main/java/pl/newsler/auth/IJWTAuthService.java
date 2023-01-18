package pl.newsler.auth;

import pl.newsler.commons.exception.InvalidUserDataException;

public interface IJWTAuthService {
    String generateJWT(UserAuthModel userAuthModel) throws IllegalArgumentException, InvalidUserDataException;
}
