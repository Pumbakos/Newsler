package pl.newsler.commons.models;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import pl.newsler.functions.EmailValidator;

import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class Email implements Serializable {
    @Serial
    private static final long serialVersionUID = -5992977381869264449L;

    @NotBlank
    @javax.validation.constraints.Email(regexp = EmailValidator.EMAIL_PATTERN)
    private final String value;
}
