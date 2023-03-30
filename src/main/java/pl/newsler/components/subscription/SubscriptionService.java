package pl.newsler.components.subscription;

import lombok.RequiredArgsConstructor;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLFirstName;
import pl.newsler.commons.model.NLLastName;
import pl.newsler.commons.model.NLNickname;
import pl.newsler.commons.model.NLToken;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.receiver.IReceiverRepository;
import pl.newsler.components.receiver.Receiver;
import pl.newsler.components.receiver.exception.ReceiverAlreadySubscribedException;
import pl.newsler.components.subscription.exception.CancellationReceiverException;
import pl.newsler.components.subscription.exception.SubscriptionTokenException;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
class SubscriptionService implements ISubscriptionService {
    private final IReceiverRepository receiverRepository;
    private final IUserRepository userRepository;

    @Override
    public void subscribe(final String subscriptionToken, final String receiverMail) throws SubscriptionTokenException, InvalidUserDataException {
        final NLToken token = NLToken.of(subscriptionToken);
        final NLEmail email = NLEmail.of(receiverMail);
        final Optional<NLUser> optionalNLUser = userRepository.findBySubscriptionToken(token);

        if (!email.validate()) {
            throw new InvalidUserDataException("Email", "Invalid");
        }
        if (!token.validate()) {
            throw new SubscriptionTokenException("Subscription token", "Invalid");
        }
        if (optionalNLUser.isEmpty()) {
            throw new SubscriptionTokenException("Subscription token", "Not associated with user");
        }

        final NLUser user = optionalNLUser.get();
        final NLUuid userUuid = user.map().getUuid();
        receiverRepository.findByUserUuidAndEmail(userUuid, email)
                .ifPresentOrElse(receiver -> {
                    throw new ReceiverAlreadySubscribedException("Association receiver -> user", "Already associated");
                }, () -> {
                    final Receiver receiver = new Receiver(
                            NLUuid.of(UUID.randomUUID()),
                            IReceiverRepository.version,
                            userUuid,
                            NLEmail.of(receiverMail),
                            NLNickname.of(""),
                            NLFirstName.of(""),
                            NLLastName.of(""),
                            false
                    );
                    receiverRepository.save(receiver);
                });
    }

    @Override
    public void cancel(final String subscriptionToken, final String receiverMail) throws SubscriptionTokenException, CancellationReceiverException {
        final NLToken token = NLToken.of(subscriptionToken);
        final NLEmail email = NLEmail.of(receiverMail);
        final Optional<NLUser> optionalNLUser = userRepository.findBySubscriptionToken(token);

        if (optionalNLUser.isEmpty()) {
            throw new SubscriptionTokenException("Cancellation token", "Not associated with user");
        }
        final NLUser user = optionalNLUser.get();
        final Optional<Receiver> optionalReceiver = receiverRepository.findByUserUuidAndEmail(user.map().getUuid(), email);

        if (optionalReceiver.isEmpty()) {
            throw new CancellationReceiverException("Receiver's email", "Not associated with user");
        }

        receiverRepository.delete(optionalReceiver.get());
    }
}
