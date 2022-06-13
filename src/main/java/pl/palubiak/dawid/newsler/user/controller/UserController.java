package pl.palubiak.dawid.newsler.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.user.service.UserService;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/v1/api/users")
class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll(){
        final List<User> all = userService.findAll();
        return all.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping("{userId}")
    public ResponseEntity<User> getUser(@PathVariable("userId") Long id) {
        User user = userService.findById(id);
        return user == null ?
                new ResponseEntity<>(HttpStatus.BAD_REQUEST) :
                new ResponseEntity<>(user, HttpStatus.OK);
    }

//    Basic bmV3YmllQG5ld3NsZXR0ZXIuaW86YXNrZjFtMDlmM200MQ==
    @GetMapping("/credentials")
    public User getUserByEmailAndPassword(@Param("token") String token){
        final String[] split = new String(Base64.getDecoder().decode(token.replace("Basic ", "")), StandardCharsets.UTF_8).split(":");
        final Optional<User> optionalUser = userService.getUserByEmailAndPassword(split[0].strip(), split[1].strip());
        return optionalUser.orElseGet(User::new);
    }

    @PostMapping("/{userId}/add/client")
    public ResponseEntity<String> addBusinessClient(@PathVariable("userId") Long userid, @Valid @RequestBody BusinessClient businessClient) {
        User byId = userService.findById(userid);
        if(byId == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        return userService.addBusinessClient(userid, businessClient) ?
                new ResponseEntity<>("Business client added successfully", HttpStatus.CREATED) :
                new ResponseEntity<>("Business client was not added", HttpStatus.BAD_REQUEST);
    }
}
