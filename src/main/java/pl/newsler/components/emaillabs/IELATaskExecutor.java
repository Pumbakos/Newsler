package pl.newsler.components.emaillabs;

import pl.newsler.commons.models.NLId;

public interface IELATaskExecutor {
    void queue(NLId userId, MailDetails details);
}
