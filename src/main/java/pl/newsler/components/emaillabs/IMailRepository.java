package pl.newsler.components.emaillabs;

import pl.newsler.commons.models.NLId;

import java.util.List;

public interface IMailRepository extends MailRepositoryJpa {
    @Override
    List<NLUserMail> findAllByUserId(NLId userId);
}
