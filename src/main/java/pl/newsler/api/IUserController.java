package pl.newsler.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserGetRequest;
import pl.newsler.components.user.dto.UserGetResponse;
import pl.newsler.components.user.dto.UserUpdateRequest;

@RequestMapping(NLApi.V1 + "/api/users")
public interface IUserController {
    @PostMapping
    ResponseEntity<UserGetResponse> get(@RequestBody UserGetRequest request);

    @PutMapping
    ResponseEntity<String> update(@RequestBody UserUpdateRequest request);

    @DeleteMapping
    ResponseEntity<String> delete(@RequestBody UserDeleteRequest request);
}
