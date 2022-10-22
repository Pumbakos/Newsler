package pl.newsler.api.definition;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.UserRequest;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/v1/api/users")
public interface IUserController {
    @GetMapping
    ResponseEntity<List<NLUser>> getAll();

    @GetMapping("{userId}")
    ResponseEntity<NLUser> getUser(@PathVariable("userId") Long id);

    //    Basic bmV3YmllQG5ld3NsZXR0ZXIuaW86YXNrZjFtMDlmM200MQ==
    @GetMapping("/credentials")
    NLUser getUserByEmailAndPassword(@Param("token") String token);

//    @PostMapping("/{userId}/client")
//    ResponseEntity<String> addBusinessClient(@PathVariable("userId") Long userid, @Valid @RequestBody BusinessClient businessClient);

    @PutMapping("/{userId}")
    ResponseEntity<String> update(@PathVariable("userId") Long userid, @Valid @RequestBody UserRequest user);
}
