package pl.newsler.components.receiver;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.newsler.commons.exception.InvalidReceiverDataException;
import pl.newsler.components.receiver.exception.ReceiverAlreadyAssociatedWithUser;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLModel;
import pl.newsler.commons.model.NLNickname;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.commons.utillity.ObjectUtils;
import pl.newsler.components.receiver.usecase.ReceiverCreateRequest;
import pl.newsler.components.receiver.usecase.ReceiverGetResponse;
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
    public String addReceiver(final ReceiverCreateRequest request, final boolean autoSaved) throws InvalidReceiverDataException {
        if (ObjectUtils.isBlank(request)) {
            throw new InvalidReceiverDataException();
        }

        final NLUuid userUuid = NLUuid.of(request.userUuid());
        final NLEmail email = NLEmail.of(request.email());
        final Optional<Receiver> optionalReceiver = receiverRepository.findByUserUuidAndEmail(userUuid, email);
        if (optionalReceiver.isPresent()) {
            if (autoSaved) {
                final Receiver receiver = optionalReceiver.get();
                receiver.setAutoSaved(false);
                receiverRepository.save(receiver);
            }
            throw new ReceiverAlreadyAssociatedWithUser();
        }

        final NLNickname nickname = NLNickname.of(request.nickname());
        final NLFirstName firstName = NLFirstName.of(request.firstName());
        final NLLastName lastName = NLLastName.of(request.lastName());
        final boolean validated = ReceiverService.validate(userUuid, email, nickname, firstName, lastName);

        if (!validated) {
            throw new InvalidReceiverDataException("Input", "Incorrect data");
        }

        final Optional<NLUser> optionalUser = userRepository.findById(NLUuid.of(request.userUuid()));
        if (optionalUser.isEmpty()) {
            throw new InvalidReceiverDataException("User", "Not found");
        }

        receiverRepository.save(new Receiver(userUuid, email, nickname, firstName, lastName, autoSaved));

        return "Receiver added successfully";
    }

    @Override
    public List<ReceiverGetResponse> fetchAllUserReceivers(final String userUuid) throws InvalidReceiverDataException {
        if (StringUtils.isBlank(userUuid)) {
            throw new InvalidReceiverDataException("UUID", "Not provided");
        }

        final NLUuid uuid = NLUuid.of(userUuid);

        final Optional<NLUser> optionalUser = userRepository.findById(uuid);
        if (optionalUser.isEmpty()) {
            throw new InvalidReceiverDataException("UUID", "Not found");
        }

        return receiverRepository.findAllByUserUuid(uuid).stream().map(Receiver::toResponse).toList();
    }

    private static boolean validate(final NLModel first, final NLModel... models) {
        AtomicBoolean validated = new AtomicBoolean(true);
        Arrays.stream(models).forEach(model -> {
            if (!model.validate()) {
                validated.set(false);
            }
        });

        return validated.get() && first.validate();
    }

}
