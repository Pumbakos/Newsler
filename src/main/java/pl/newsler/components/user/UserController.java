package pl.newsler.components.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.IUserController;
import pl.newsler.components.user.dto.UserGetRequest;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;

@RestController
@RequiredArgsConstructor
class UserController implements IUserController {
    private final IUserCrudService userService;

    @Override
    public ResponseEntity<NLDUser> get(UserGetRequest request) {
        NLDUser user = userService.get(request);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> update(UserUpdateRequest request) {
        userService.update(request);
        return new ResponseEntity<>("User updated", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> delete(UserDeleteRequest request) {
        userService.delete(request);
        return new ResponseEntity<>("User deleted", HttpStatus.OK);
    }
}
