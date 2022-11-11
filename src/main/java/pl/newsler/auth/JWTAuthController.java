package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.newsler.api.IJWTAuthController;
import pl.newsler.api.exceptions.UnauthorizedException;
import pl.newsler.components.user.UserDataNotFineException;

@RequiredArgsConstructor
class JWTAuthController implements IJWTAuthController {
    private final IJWTAuthService jwtService;

    @GetMapping
    @Override
    public ResponseEntity<String> generateJWT(@RequestBody UserAuthModel userAuthModel) throws UnauthorizedException {
        try {
            final String jwt = jwtService.generateJWT(userAuthModel);
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (IllegalArgumentException | UserDataNotFineException e) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
}
