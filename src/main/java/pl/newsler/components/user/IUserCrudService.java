package pl.newsler.components.user;

import jakarta.validation.constraints.NotNull;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.exception.UserAlreadyExistsException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.components.user.dto.UserDeleteRequest;
import pl.newsler.components.user.dto.UserGetRequest;
import pl.newsler.components.user.dto.UserGetResponse;
import pl.newsler.components.user.dto.UserUpdateRequest;

public interface IUserCrudService {
    UserGetResponse get(UserGetRequest request) throws InvalidUserDataException;

    void update(final UserUpdateRequest request) throws InvalidUserDataException;

    @NotNull NLUuid create(NLFirstName name, NLLastName lastName, NLEmail email, NLPassword password) throws UserAlreadyExistsException;

    void delete(UserDeleteRequest request) throws InvalidUserDataException;
}
