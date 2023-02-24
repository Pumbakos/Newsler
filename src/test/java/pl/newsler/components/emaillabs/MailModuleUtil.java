package pl.newsler.components.emaillabs;

import org.jetbrains.annotations.NotNull;
import pl.newsler.components.emaillabs.usecase.ELAInstantMailRequest;
import pl.newsler.components.emaillabs.usecase.ELAScheduleMailRequest;
import pl.newsler.components.user.NLUser;

import java.util.List;

public class MailModuleUtil {
    private MailModuleUtil() {
        // non-instantiable class
    }

    @NotNull
    public static ELAInstantMailRequest createInstantMailRequest(List<NLUser> users, NLUser user) {
        return new ELAInstantMailRequest(
                user.getEmail().getValue(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                "MOCK TEST",
                "MOCK TEST MESSAGE"
        );
    }

    @NotNull
    public static ELAScheduleMailRequest createScheduledMailRequest(List<NLUser> users, NLUser user, String dateTime, String zoneId) {
        return new ELAScheduleMailRequest(
                user.getEmail().getValue(),
                List.of(users.get(1).getEmail().getValue(), users.get(2).getEmail().getValue()),
                "MOCK TEST",
                "MOCK TEST MESSAGE",
                dateTime,
                zoneId
        );
    }
}
