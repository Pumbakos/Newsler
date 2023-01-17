package pl.newsler.auth;

import pl.newsler.api.exceptions.UserDataNotFineException;

public interface IJWTAuthService {
    String generateJWT(UserAuthModel userAuthModel) throws IllegalArgumentException, UserDataNotFineException;
}
