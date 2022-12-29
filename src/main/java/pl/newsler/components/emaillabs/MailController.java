package pl.newsler.components.emaillabs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.newsler.api.NLApi;
import pl.newsler.components.emaillabs.dto.MailSendRequest;

import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequiredArgsConstructor
@RequestMapping(NLApi.V1 + "/api/mails")
public class MailController {
    private final MailService service;

    @PostMapping
    public ResponseEntity<HttpStatus> queue(@RequestBody MailSendRequest request) {
        service.queue(request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping
    public ResponseEntity<MailSendRequest> test() {
        return new ResponseEntity<>(new MailSendRequest("", List.of("newslerowsky@app.co.devenv"), Collections.emptyList(), Collections.emptyList(), "TEST", "TEST"), HttpStatus.OK);
    }
}
