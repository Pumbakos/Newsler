package pl.newsler.components.subscription;

import lombok.RequiredArgsConstructor;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLToken;
import pl.newsler.components.receiver.IReceiverRepository;
import pl.newsler.components.receiver.Receiver;
import pl.newsler.components.subscription.exception.CancellationReceiverException;
import pl.newsler.components.subscription.exception.CancellationTokenException;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;

import java.util.Optional;

@RequiredArgsConstructor
class SubscriptionService implements ISubscriptionService {
    private final IReceiverRepository receiverRepository;
    private final IUserRepository userRepository;

    @Override
    public void cancel(final String cancellationToken, final String receiverMail) throws CancellationTokenException, CancellationReceiverException {
        final NLToken token = NLToken.of(cancellationToken);
        final NLEmail email = NLEmail.of(receiverMail);
        final Optional<NLUser> optionalNLUser = userRepository.findByCancellationToken(token);

        if (optionalNLUser.isEmpty()) {
            throw new CancellationTokenException("Cancellation token", "Not associated with user");
        }
        final NLUser user = optionalNLUser.get();
        final Optional<Receiver> optionalReceiver = receiverRepository.findByUserUuidAndEmail(user.map().getId(), email);

        if (optionalReceiver.isEmpty()) {
            throw new CancellationReceiverException("Receiver's email", "Not associated with user");
        }

        receiverRepository.delete(optionalReceiver.get());
    }
}
