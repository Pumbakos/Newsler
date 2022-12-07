package pl.newsler.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.commons.models.ApiVersion;
import pl.newsler.commons.models.NLId;
import pl.newsler.components.user.NLDUser;
import pl.newsler.components.user.dto.UserCreateRequest;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;

@CrossOrigin(origins = "*")
@RequestMapping(ApiVersion.V1 + "/api/users")
@RestController
public interface IUserController {
    @GetMapping("/{userId}")
    ResponseEntity<NLDUser> getById(@PathVariable("userId") NLId id);

    ResponseEntity<NLId> create(UserCreateRequest request);

    ResponseEntity<HttpStatus> update(UserUpdateRequest request);

    ResponseEntity<HttpStatus> delete(UserDeleteRequest request);
}
