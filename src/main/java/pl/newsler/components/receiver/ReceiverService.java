package pl.newsler.components.receiver;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.newsler.commons.exception.InvalidReceiverDataException;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLFirstName;
import pl.newsler.commons.models.NLLastName;
import pl.newsler.commons.models.NLModel;
import pl.newsler.commons.models.NLNickname;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.utillity.ObjectUtils;
import pl.newsler.components.receiver.dto.ReceiverCreateRequest;
import pl.newsler.components.receiver.dto.ReceiverGetResponse;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
class ReceiverService implements IReceiverService {
    private final IReceiverRepository receiverRepository;
    private final IUserRepository userRepository;

    @Override
    public String add(final ReceiverCreateRequest request, boolean autoSaved) throws InvalidReceiverDataException {
        if (ObjectUtils.isBlank(request)) {
            throw new InvalidReceiverDataException();
        }

        final NLUuid uuid = NLUuid.of(request.userUuid());
        final NLEmail email = NLEmail.of(request.email());
        final NLNickname nickname = NLNickname.of(request.nickname());
        final NLFirstName firstName = NLFirstName.of(request.firstName());
        final NLLastName lastName = NLLastName.of(request.lastName());
        final boolean validated = ReceiverService.validate(uuid, email, nickname, firstName, lastName);

        if (!validated) {
            throw new InvalidReceiverDataException("Input", "Incorrect data");
        }

        receiverRepository.save(new Receiver(uuid, email, nickname, firstName, lastName, autoSaved));

        return "Receiver added successfully";
    }

    @Override
    public List<ReceiverGetResponse> fetchAllUserReceivers(String userUuid) throws InvalidReceiverDataException {
        if (StringUtils.isBlank(userUuid)) {
            throw new InvalidReceiverDataException("UUID", "Not provided");
        }

        final NLUuid uuid = NLUuid.of(userUuid);
        if (!uuid.validate()) {
            throw new InvalidReceiverDataException("UUID", "Invalid format");
        }

        final Optional<NLUser> optionalUser = userRepository.findById(uuid);
        if (optionalUser.isEmpty()) {
            throw new InvalidReceiverDataException("UUID", "Not found");
        }

        return receiverRepository.findAllByUserUuid(uuid).stream().map(Receiver::toResponse).toList();
    }

    private static boolean validate(NLModel first, NLModel... models) {
        if (models == null || models.length == 0) {
            return first != null && first.validate();
        }
        AtomicBoolean validated = new AtomicBoolean(true);
        Arrays.stream(models).forEach(model -> {
            if (model == null || !model.validate()) {
                validated.set(false);
            }
        });

        return validated.get();
    }

}
