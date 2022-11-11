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

@RequiredArgsConstructor
class UserController implements IUserController {
    private final IUserCrudService userService;

    @GetMapping("/{userId}")
    @Override
    public ResponseEntity<NLDUser> getUser(@PathVariable("userId") NLId id) {
        try {
            NLDUser user = userService.getById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserDataNotFineException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
