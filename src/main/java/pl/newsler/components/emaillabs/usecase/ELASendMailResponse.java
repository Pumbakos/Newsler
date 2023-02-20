package pl.newsler.components.emaillabs.usecase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ELASendMailResponse {
    private int code;
    private String status;
    private String message;
    private String data;
    @JsonProperty(value = "req_id")
    private String reqId;
}
