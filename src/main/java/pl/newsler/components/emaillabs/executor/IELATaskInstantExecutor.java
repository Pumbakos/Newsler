package pl.newsler.components.emaillabs.executor;

import pl.newsler.commons.model.NLUuid;

public interface IELATaskInstantExecutor extends IELATaskExecutor {
    void queue(NLUuid userId, ELAInstantMailDetails details);
}
