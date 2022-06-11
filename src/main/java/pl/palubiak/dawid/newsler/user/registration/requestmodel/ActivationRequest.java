package pl.palubiak.dawid.newsler.user.registration.requestmodel;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class ActivationRequest {
    private final String email;
    private final String password;
}
