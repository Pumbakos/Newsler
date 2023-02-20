package pl.newsler.components.emaillabs;

import pl.newsler.commons.model.NLUuid;

import java.util.List;

public interface IELAMailRepository extends ELAMailRepositoryJpa {
    @Override
    List<ELAUserMail> findAllByUserId(NLUuid userId);
}
