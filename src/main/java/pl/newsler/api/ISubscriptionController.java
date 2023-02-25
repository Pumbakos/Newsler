package pl.newsler.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(NLApi.V1 + "/subscription")
public interface ISubscriptionController {
    @PostMapping("/cancel")
    ResponseEntity<String> a(@RequestParam("token") String token);
}
