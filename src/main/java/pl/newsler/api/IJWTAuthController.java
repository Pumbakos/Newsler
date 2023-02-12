package pl.newsler.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.newsler.auth.UserAuthModel;

@RequestMapping(NLApi.V1 + "/api/auth/jwt")
public interface IJWTAuthController {
    @PostMapping
    ResponseEntity<String> generateJWT(@RequestBody UserAuthModel userAuthModel);
}
