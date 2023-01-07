package pl.newsler.components.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.IUserController;
import pl.newsler.api.exceptions.Advised;
import pl.newsler.commons.models.NLId;
import pl.newsler.components.user.dto.UserCreateRequest;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;

@Advised
@RestController
@RequiredArgsConstructor
class UserController implements IUserController {
    private final IUserCrudService userService;

    @Override
    public ResponseEntity<NLDUser> getById(String id) {
        NLDUser user = userService.getById(NLId.of(id));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<NLId> create(UserCreateRequest request) {
        NLId nlId = userService.create(request.name(), request.lastName(), request.email(), request.password());
        return new ResponseEntity<>(nlId, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> update(UserUpdateRequest request) {
        userService.update(request.id(), request.appKey(), request.secretKey(), request.smtpAccount());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> delete(UserDeleteRequest request) {
        userService.delete(request.id(), request.password());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
