package pl.newsler.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.newsler.components.user.NLDUser;
import pl.newsler.components.user.dto.GetUserRequest;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;

@CrossOrigin(origins = "*")
@RequestMapping(NLApi.V1 + "/api/users")
public interface IUserController {
    @GetMapping("/{userId}")
    ResponseEntity<NLDUser> getByEmail(@RequestBody GetUserRequest request);

    @PutMapping
    ResponseEntity<HttpStatus> update(@RequestBody UserUpdateRequest request);

    @DeleteMapping
    ResponseEntity<HttpStatus> delete(@RequestBody UserDeleteRequest request);
}
