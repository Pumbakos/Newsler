package pl.palubiak.dawid.newsler.businesclinet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.businesclinet.service.BusinessClientService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/clients")
public class BusinessClientController {
    private final BusinessClientService businessClientService;

    @Autowired
    public BusinessClientController(BusinessClientService businessClientService) {
        this.businessClientService = businessClientService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<BusinessClient>> getAllClients() {
        return new ResponseEntity<>(businessClientService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessClient> getClientById(@PathVariable("id") Long id) {
        BusinessClient businessClient = businessClientService.findById(id);
        return businessClient == null ?
                new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(businessClient, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    public ResponseEntity<BusinessClient> getClientByEmail(@PathVariable("email") String email) {
        BusinessClient businessClient = businessClientService.findByEmail(email);
        return businessClient == null ?
                new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(businessClient, HttpStatus.OK);
    }

    @GetMapping("/all/subscribed")
    public ResponseEntity<List<BusinessClient>> getAllSubscribers() {
        return new ResponseEntity<>(businessClientService.findAllSubscribers(), HttpStatus.OK);
    }

    @GetMapping("/all/newsletters")
    public ResponseEntity<List<BusinessClient>> getAllNewsletters() {
        return new ResponseEntity<>(businessClientService.findAllNewsletterers(), HttpStatus.OK);
    }

    @GetMapping("/all/partnership")
    public ResponseEntity<List<BusinessClient>> getAllPartnershipMembers() {
        return new ResponseEntity<>(businessClientService.findAllPartnershipMembers(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody BusinessClient businessClient) {
        return businessClientService.save(businessClient) == null ?
                new ResponseEntity<>("Client could not be saved, check data", HttpStatus.BAD_REQUEST) :
                new ResponseEntity<>("Client saved successfully", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Long id, @RequestBody BusinessClient businessClient) {
        return businessClientService.update(id, businessClient) ?
                new ResponseEntity<>("Client updated successfully", HttpStatus.OK) :
                new ResponseEntity<>("Client could not be updated, check data or ID", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        return businessClientService.delete(id) ?
                new ResponseEntity<>("Client deleted successfully", HttpStatus.OK) :
                new ResponseEntity<>("Client could not be deleted, check ID", HttpStatus.BAD_REQUEST);
    }
}
