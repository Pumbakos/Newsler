package pl.newsler.components.user;

import jakarta.validation.constraints.NotNull;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.components.signup.exception.UserAlreadyExistsException;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLPassword;
import pl.newsler.components.user.usecase.UserDeleteRequest;
import pl.newsler.components.user.usecase.UserGetRequest;
import pl.newsler.components.user.usecase.UserGetResponse;
import pl.newsler.components.user.usecase.UserUpdateRequest;

public interface IUserCrudService {
    UserGetResponse get(UserGetRequest request) throws InvalidUserDataException;

    void update(final UserUpdateRequest request) throws InvalidUserDataException;

    @NotNull NLUuid create(NLFirstName name, NLLastName lastName, NLEmail email, NLPassword password) throws UserAlreadyExistsException;

    void delete(UserDeleteRequest request) throws InvalidUserDataException;
}
