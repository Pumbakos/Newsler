package pl.newsler.components.user;

import jakarta.validation.constraints.NotNull;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.exception.UserAlreadyExistsException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.components.user.dto.GetUserRequest;
import pl.newsler.components.user.dto.UserUpdateRequest;

public interface IUserCrudService {
    NLDUser get(GetUserRequest request) throws InvalidUserDataException;

    void update(final UserUpdateRequest request) throws InvalidUserDataException;

    @NotNull NLUuid create(NLFirstName name, NLLastName lastName, NLEmail email, NLPassword password) throws UserAlreadyExistsException;

    void delete(NLUuid id, NLPassword password) throws InvalidUserDataException;
}
