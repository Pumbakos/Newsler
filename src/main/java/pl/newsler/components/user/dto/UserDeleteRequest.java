package pl.newsler.components.user.dto;

import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLPassword;

public record UserDeleteRequest(NLUuid id, NLPassword password) {
}
