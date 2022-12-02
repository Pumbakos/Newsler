package pl.newsler.components.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.newsler.api.IUserController;
import pl.newsler.commons.models.NLId;
import pl.newsler.components.user.IUserCrudService;
import pl.newsler.components.user.NLDUser;
import pl.newsler.components.user.UserDataNotFineException;
import pl.newsler.components.user.models.UserCreateRequest;
import pl.newsler.components.user.models.UserDeleteRequest;
import pl.newsler.components.user.models.UserUpdateRequest;

@RequiredArgsConstructor
class UserController implements IUserController {
    private final IUserCrudService userService;

    @GetMapping("/{userId}")
    @Override
    public ResponseEntity<NLDUser> getById(@PathVariable("userId") NLId id) {
        try {
            NLDUser user = userService.getById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserDataNotFineException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<NLId> create(UserCreateRequest request) {
        try {
            NLId nlId = userService.create(request.name(), request.lastName(), request.email(), request.password());
            return new ResponseEntity<>(nlId, HttpStatus.OK);
        } catch (UserDataNotFineException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<HttpStatus> update(UserUpdateRequest request) {
        try {
            userService.update(request.id(), request.appKey(), request.secretKey(), request.smtpAccount());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserDataNotFineException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<HttpStatus> delete(UserDeleteRequest request) {
        try {
            userService.delete(request.id(), request.password());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserDataNotFineException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
