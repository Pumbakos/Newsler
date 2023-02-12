package pl.newsler.components.emaillabs;

import pl.newsler.commons.models.NLUuid;

public interface IELATaskExecutor {
    void queue(NLUuid userId, ELAMailDetails details);
}
