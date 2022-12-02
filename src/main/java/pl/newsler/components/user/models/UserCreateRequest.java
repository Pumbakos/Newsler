package pl.newsler.components.user.models;

import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;

public record UserCreateRequest(NLFirstName name, NLLastName lastName, NLEmail email, NLPassword password) {
}
