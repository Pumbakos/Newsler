package pl.newsler.auth;

import pl.newsler.components.user.UserDataNotFineException;

public interface IJWTAuthService {
    String generateJWT(UserAuthModel userAuthModel) throws IllegalArgumentException, UserDataNotFineException;
}
