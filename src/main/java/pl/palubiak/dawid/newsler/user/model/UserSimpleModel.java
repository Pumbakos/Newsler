package pl.palubiak.dawid.newsler.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class UserSimpleModel {
    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    public boolean isValid() {
        return !this.email.isBlank() && !this.name.isBlank() && !this.password.isBlank();
    }
}
