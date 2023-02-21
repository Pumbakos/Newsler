package pl.newsler.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.components.emaillabs.exception.InvalidDateException;
import pl.newsler.components.emaillabs.usecase.ELAGetMailResponse;
import pl.newsler.components.emaillabs.usecase.ELAMailSendRequest;
import pl.newsler.components.emaillabs.usecase.ELAMailScheduleRequest;

import java.util.List;

@RequestMapping(NLApi.V1 + "/api/mails")
public interface IELAMailController {
    @PostMapping("/instant")
    ResponseEntity<HttpStatus> queueAndExecute(@RequestBody ELAMailSendRequest request) throws InvalidUserDataException;

    @PostMapping("/scheduled")
    ResponseEntity<HttpStatus> schedule(@RequestBody ELAMailScheduleRequest request) throws InvalidUserDataException, InvalidDateException;

    @GetMapping("/{userId}")
    ResponseEntity<List<ELAGetMailResponse>> fetchAllMails(@PathVariable("userId") String userId) throws InvalidUserDataException;
}
