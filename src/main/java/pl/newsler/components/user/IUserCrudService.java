package pl.newsler.components.user;

import jakarta.validation.constraints.NotNull;
import pl.newsler.api.exceptions.UserDataNotFineException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.components.user.dto.GetUserRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;

public interface IUserCrudService {
    NLDUser get(GetUserRequest request) throws UserDataNotFineException;

    void update(final UserUpdateRequest request) throws UserDataNotFineException;

    @NotNull NLUuid create(NLFirstName name, NLLastName lastName, NLEmail email, NLPassword password);

    void delete(NLUuid id, NLPassword password) throws UserDataNotFineException;
}
