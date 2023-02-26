package pl.newsler.components.emaillabs.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * <a href="https://dev.emaillabs.io/#api-Send-add_template">Email Labs API add template</a>
 */
@Getter
@Setter
public class ELATemplateAddResponse {
    private int code;
    private String status;
    private String message;
    private Data data;
    @JsonProperty(value = "req_id")
    private String reqId;


    @JsonIgnore
    public @NotNull String getTemplateId() {
        if (data == null) {
            return "";
        }

        return data.getTemplateId();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        @JsonProperty("template_id")
        String templateId = "";
    }
}
