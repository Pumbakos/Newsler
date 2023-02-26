package pl.newsler.components.subscription;

import pl.newsler.components.subscription.exception.CancellationReceiverException;
import pl.newsler.components.subscription.exception.CancellationTokenException;

public interface ISubscriptionService {
    void cancel(final String cancellationToken, final String receiverMail) throws CancellationTokenException, CancellationReceiverException;
}
