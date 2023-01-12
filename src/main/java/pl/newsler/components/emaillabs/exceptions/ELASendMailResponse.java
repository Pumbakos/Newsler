package pl.newsler.components.emaillabs.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class ELASendMailResponse {
    private int code;
    private String status;
    private String message;
    private String data;
    @JsonProperty(value = "req_id")
    private String reqId;
}
