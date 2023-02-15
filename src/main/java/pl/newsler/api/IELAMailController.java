package pl.newsler.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.components.emaillabs.dto.ELAGetMailResponse;
import pl.newsler.components.emaillabs.dto.ELAMailSendRequest;

import java.util.List;

@RequestMapping(NLApi.V1 + "/api/mails")
public interface IELAMailController {
    @PostMapping
    ResponseEntity<HttpStatus> queue(@RequestBody ELAMailSendRequest request) throws InvalidUserDataException;

    @GetMapping("/{userId}")
    ResponseEntity<List<ELAGetMailResponse>> fetchAllMails(@PathVariable("userId") String userId) throws InvalidUserDataException;
}
