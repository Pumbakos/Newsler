package pl.newsler.components.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.IUserController;
import pl.newsler.components.user.dto.GetUserRequest;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;

@RestController
@RequiredArgsConstructor
class UserController implements IUserController {
    private final IUserCrudService userService;

    @Override
    public ResponseEntity<NLDUser> getByEmail(GetUserRequest request) {
        NLDUser user = userService.get(request);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> update(UserUpdateRequest request) {
        userService.update(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> delete(UserDeleteRequest request) {
        userService.delete(request.id(), request.password());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
