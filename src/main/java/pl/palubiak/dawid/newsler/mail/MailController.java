package pl.palubiak.dawid.newsler.mail;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.palubiak.dawid.newsler.mail.model.MailRequest;
import pl.palubiak.dawid.newsler.mail.pznu.MailSenderService;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/v1/api/mails")
@AllArgsConstructor
public class MailController {
    private final MailSenderService mailSenderService;

    @PostMapping
//    @GetMapping
    public ResponseEntity<String> sendMail(@RequestBody MailRequest request){
//        MailRequest request = new MailRequest();
//        request.setFrom("info@newsler.com");
//        request.setFromName("Info Newsler");
//        request.setTo("newbie@localhost.com");
//        request.setSubject("Test Mock Mail");
//        request.setText("Make Your App More Secure\n" +
//                "Notice that we only watch for sign-in and sign-out actions to update the authenticated state. This works for our case, but you might not want to allow users whose sessions have timed out to continue playing tic-tac-toe. To make your app more secure, you need to watch for sessions timing out. There are several ways to do this, but some popular ones include subscribing to Router events and checking. Or, if your app makes calls to a back-end, redirect the user to the login page when the HTTP call returns a 401 response code.\n" +
//                "\n" +
//                "Make Angular Tests Pass With Angular Material\n" +
//                "You generated a lot of code in this tutorial. When you created components, tests were created for those components as well. The tests merely verify that the components render. If you run ng test, most of them will fail because the tests have neither the imports for the components you added nor a provider for the OktaAuth object. If you’d like to see what it takes to make all the tests pass, look at this commit to add the Angular Material library imports, and this commit to provide a fake OktaAuth object.\n" +
//                "\n" +
//                "Learn More About Angular Material and Secure Login\n" +
//                "In this tutorial, I showed you how to implement your own login form in an Angular application using Material Design and the Angular Material library. Coding up your own form may be a viable option if you want to present a uniform user experience. Much of this tutorial can be used for other design libraries and is not limited to Material Design but Google’s Material Design standard is probably one of the most recognized user interface standards nowadays. Using it will improve the usability of your web application.\n" +
//                "\n");

        try {
            mailSenderService.send(request, request.getCc() != null, request.getBcc() != null);
            return new ResponseEntity<>("Mail is being proceed", HttpStatus.ACCEPTED);
        } catch (MessagingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
