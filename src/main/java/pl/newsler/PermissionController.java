package pl.newsler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PermissionController {
    @GetMapping("test1")
    public ResponseEntity<String> test1() {
        return new ResponseEntity<>("TEST 1", HttpStatus.OK);
    }

    @GetMapping("test2")
    public ResponseEntity<String> test2() {
        return new ResponseEntity<>("TEST 2", HttpStatus.OK);
    }

    @GetMapping("test3")
    public ResponseEntity<String> test3() {
        return new ResponseEntity<>("TEST 3", HttpStatus.OK);
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello MDFC", HttpStatus.OK);
    }
}
