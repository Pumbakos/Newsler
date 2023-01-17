package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.IJWTAuthController;
import pl.newsler.api.exceptions.UserDataNotFineException;

@RestController
@RequiredArgsConstructor
class JWTAuthController implements IJWTAuthController {
    private final IJWTAuthService jwtService;

    @Override
    public ResponseEntity<String> generateJWT(UserAuthModel userAuthModel) {
        try {
            final String jwt = jwtService.generateJWT(userAuthModel);
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (IllegalArgumentException | UserDataNotFineException e) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
}
