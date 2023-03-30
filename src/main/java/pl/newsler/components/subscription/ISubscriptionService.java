package pl.newsler.components.subscription;

import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.components.subscription.exception.CancellationReceiverException;
import pl.newsler.components.subscription.exception.SubscriptionTokenException;

public interface ISubscriptionService {
    void cancel(final String subscriptionToken, final String receiverMail) throws SubscriptionTokenException, CancellationReceiverException;

    void subscribe(final String subscriptionToken, final String receiverMail) throws SubscriptionTokenException, InvalidUserDataException;
}
