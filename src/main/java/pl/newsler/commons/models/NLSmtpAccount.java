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
public class NLSmtpAccount implements NLModel, Serializable {
    @Serial
    private static final long serialVersionUID = -8440638731256566536L;

    private final String value;

    @Override
    public boolean validate() {
        return StringUtils.isNotBlank(value) && value.matches("^\\d.(a-z){3,}.smtp$");
    }
}
