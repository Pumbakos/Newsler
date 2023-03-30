package pl.newsler.auth;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.IJWTAuthController;
import pl.newsler.commons.exception.InvalidUserDataException;

@RestController
@RequiredArgsConstructor
class JWTAuthController implements IJWTAuthController {
    private final IJWTAuthService jwtService;
    private final Gson gson = new Gson();

    @Override
    public ResponseEntity<String> generateJWT(UserAuthModel userAuthModel) {
        try {
            final String jwt = jwtService.generateJWT(userAuthModel);
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (IllegalArgumentException | InvalidUserDataException e) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
}
