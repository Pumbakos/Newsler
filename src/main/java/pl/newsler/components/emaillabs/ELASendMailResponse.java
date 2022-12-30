package pl.newsler.components.emaillabs;

import lombok.Value;

/**
 * ELA - EmailLabs API
 */
@Value
public class ELASendMailResponse {
    int code;
    String status;
    String message;
    Object data;

    @SuppressWarnings("java:S116") // EmailLabs API response returns such field
    String req_id;
}
