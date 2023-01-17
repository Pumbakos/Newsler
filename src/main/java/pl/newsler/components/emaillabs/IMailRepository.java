package pl.newsler.components.emaillabs;

import pl.newsler.commons.models.NLUuid;

import java.util.List;

public interface IMailRepository extends MailRepositoryJpa {
    @Override
    List<NLUserMail> findAllByUserId(NLUuid userId);
}
