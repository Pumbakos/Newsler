package pl.newsler.commons.models;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class NLAppKey implements NLModel, Serializable {
    @Serial
    private static final long serialVersionUID = -6828434776883712910L;

    private final String value;

    public boolean validate() {
        return StringUtils.isNotBlank(value) && value.matches("^[a-zA-Z\\d]{40}$");
    }
}
