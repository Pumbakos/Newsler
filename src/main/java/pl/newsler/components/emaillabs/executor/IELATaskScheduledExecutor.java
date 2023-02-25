package pl.newsler.components.emaillabs.executor;

import pl.newsler.commons.model.NLUuid;

public interface IELATaskScheduledExecutor extends IELATaskExecutor {
    void schedule(NLUuid userId, ELAScheduleMailDetails details);
}
