package pl.newsler.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.newsler.components.user.usecase.UserDeleteRequest;
import pl.newsler.components.user.usecase.UserGetRequest;
import pl.newsler.components.user.usecase.UserGetResponse;
import pl.newsler.components.user.usecase.UserUpdateRequest;

@RequestMapping(NLApi.V1 + "/api/users")
public interface IUserController {
    @PostMapping
    ResponseEntity<UserGetResponse> get(@RequestBody UserGetRequest request);

    @PutMapping
    ResponseEntity<String> update(@RequestBody UserUpdateRequest request);

    @DeleteMapping
    ResponseEntity<String> delete(@RequestBody UserDeleteRequest request);
}
