package pl.newsler.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.ApplicationScope;

@Controller
@ApplicationScope
@RequiredArgsConstructor
@RequestMapping("api/jwt")
public class JWTController {
    private final JWTService jwtService;

    @GetMapping
    public ResponseEntity<String> generateJWT(@RequestBody UserAuthModel userAuthModel){
        final String jwt = jwtService.generateJWT(userAuthModel);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }
}
