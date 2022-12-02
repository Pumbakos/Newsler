package pl.newsler.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.commons.models.NLId;
import pl.newsler.components.user.NLDUser;
import pl.newsler.components.user.models.UserCreateRequest;
import pl.newsler.components.user.models.UserDeleteRequest;
import pl.newsler.components.user.models.UserUpdateRequest;
import pl.newsler.security.interceptor.RequestSecured;

@CrossOrigin(origins = "*")
@RequestMapping("/v1/api/users")
@RestController
//@RequestSecured
public interface IUserController {
    @GetMapping("/{userId}")
    ResponseEntity<NLDUser> getById(@PathVariable("userId") NLId id);

    ResponseEntity<NLId> create(UserCreateRequest request);

    ResponseEntity<HttpStatus> update(UserUpdateRequest request);

    ResponseEntity<HttpStatus> delete(UserDeleteRequest request);
}
