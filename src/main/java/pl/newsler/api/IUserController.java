package pl.newsler.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.newsler.commons.models.NLId;
import pl.newsler.components.user.NLDUser;
import pl.newsler.components.user.dto.UserCreateRequest;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;

@CrossOrigin(origins = "*")
@RequestMapping(NLApi.V1 + "/api/users")
public interface IUserController {
    @GetMapping("/{userId}")
    ResponseEntity<NLDUser> getById(@PathVariable("userId") String id);

    @PostMapping
    ResponseEntity<NLId> create(@RequestBody UserCreateRequest request);

    @PutMapping
    ResponseEntity<HttpStatus> update(@RequestBody UserUpdateRequest request);

    @DeleteMapping
    ResponseEntity<HttpStatus> delete(@RequestBody UserDeleteRequest request);
}
