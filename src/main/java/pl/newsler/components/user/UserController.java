package pl.newsler.components.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.IUserController;
import pl.newsler.commons.models.NLId;
import pl.newsler.components.user.dto.UserCreateRequest;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;

@RestController
@RequiredArgsConstructor
class UserController implements IUserController {
    private final IUserCrudService userService;

    @Override
    public ResponseEntity<NLDUser> getById(String id) {
        try {
            NLDUser user = userService.getById(NLId.of(id));
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
