package pl.newsler.components.emaillabs.usecase;

import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.jetbrains.annotations.NotNull;
import pl.newsler.commons.model.NLEmail;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ELASendWithTemplate {
    private Map<String, Map<String, Map<String, String>>> to = new HashMap<>();
    private String smtp_account;
    private int new_structure = 1;
    private String subject;
    private String template_id;
    private String from;
    private String from_name;

    @JsonIgnore
    private String key;

    public void appendReceiverEmail(NLEmail email){
        key = email.getValue();
        to.put(key, new HashMap<>());
    }

    public void appendVar(@NotNull String varKey, @NotNull String varName) {
        final Map<String, Map<String, String>> map = to.get(key);
        Map<String, String> vars = map.get("vars");
        if (vars == null) {
            HashMap<String, String> values = new HashMap<>();
            values.put(varKey, varName);
            map.put("vars", values);
        } else {
            vars.put(varKey, varName);
        }
    }
}
