package pl.newsler.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.newsler.auth.UserAuthModel;

@CrossOrigin(origins = "*")
@RequestMapping("/api/jwt")
public interface IJWTAuthController {
    @PostMapping
    ResponseEntity<String> generateJWT(@RequestBody UserAuthModel userAuthModel);
}
