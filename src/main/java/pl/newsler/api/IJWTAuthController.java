package pl.newsler.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.exceptions.UnauthorizedException;
import pl.newsler.auth.UserAuthModel;

@RestController
@RequestMapping("/api/jwt")
public interface IJWTAuthController {

    @GetMapping
    ResponseEntity<String> generateJWT(@RequestBody UserAuthModel userAuthModel) throws UnauthorizedException;
}
