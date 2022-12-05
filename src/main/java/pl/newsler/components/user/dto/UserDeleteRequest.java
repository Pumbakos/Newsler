package pl.newsler.components.user.dto;

import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLPassword;

public record UserDeleteRequest(NLId id, NLPassword password) {
}
