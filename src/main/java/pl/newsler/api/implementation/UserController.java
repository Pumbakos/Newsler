package pl.newsler.api.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.commons.models.NLId;
import pl.newsler.components.user.IUserCrudService;
import pl.newsler.components.user.NLDUser;
import pl.newsler.components.user.UserDataNotFineException;
import pl.newsler.security.interceptor.RequestSecured;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/api/users")
@RestController
@RequestSecured
@RequiredArgsConstructor
public class UserController {
    private final IUserCrudService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<NLDUser> getUser(@PathVariable("userId") NLId id) {
        try {
            NLDUser user = userService.getById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserDataNotFineException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
