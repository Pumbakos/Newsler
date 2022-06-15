package pl.palubiak.dawid.newsler.mail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.palubiak.dawid.newsler.user.registration.EmailValidator;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MailRequest {
    @NotBlank
    @Email(regexp = EmailValidator.EMAIL_PATTERN)
    private String from;

    @NotBlank
    @Email(regexp = EmailValidator.EMAIL_PATTERN)
    private String[] to;

    @Email(regexp = EmailValidator.EMAIL_PATTERN)
    private String[] bcc;

    @Email(regexp = EmailValidator.EMAIL_PATTERN)
    private String[] cc;

    @NotBlank
    private String subject;

    @NotBlank
    private String text;
}
