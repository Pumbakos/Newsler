package pl.newsler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jwt")
class JWTAuthController {
    private final JWTAuthService jwtService;

    @GetMapping
    public ResponseEntity<String> generateJWT(@RequestBody UserAuthModel userAuthModel) {
        final String jwt = jwtService.generateJWT(userAuthModel);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }
}
