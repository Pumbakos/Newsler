package pl.palubiak.dawid.newsler.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.user.model.UserSimpleModel;
import pl.palubiak.dawid.newsler.user.service.UserService;

import javax.validation.Valid;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users")
class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody UserSimpleModel userSimpleModel) {
        Optional<User> optionalUser = userService.save(userSimpleModel);
        return optionalUser.isPresent() ?
                new ResponseEntity<>("User created successfully", HttpStatus.CREATED) :
                new ResponseEntity<>("User was not created", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<User> getUser(@RequestParam("id") Long id) {
        User user = userService.findById(id);
        return user == null ?
                new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(user, HttpStatus.OK);
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
