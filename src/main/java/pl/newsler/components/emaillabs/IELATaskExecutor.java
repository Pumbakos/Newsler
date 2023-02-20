package pl.newsler.components.emaillabs;

import pl.newsler.commons.model.NLUuid;

public interface IELATaskExecutor {
    void queue(NLUuid userId, ELAMailDetails details);
}
