package pl.newsler.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.commons.models.NLId;
import pl.newsler.components.user.NLDUser;
import pl.newsler.security.interceptor.RequestSecured;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/api/users")
@RestController
@RequestSecured
public interface IUserController {
    @GetMapping("/{userId}")
    ResponseEntity<NLDUser> getUser(@PathVariable("userId") NLId id);
}
