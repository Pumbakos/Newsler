package pl.palubiak.dawid.newsler.businesclinet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.businesclinet.repository.BusinessClientRepository;
import pl.palubiak.dawid.newsler.utils.PresenceChecker;
import pl.palubiak.dawid.newsler.utils.UpdateUtils;

import java.util.List;
import java.util.Optional;

@Service
public class BusinessClientService {
    private final BusinessClientRepository businessClientRepository;
    private final UpdateUtils<BusinessClient> updateUtils;
    private final PresenceChecker<BusinessClient> presenceChecker;

    @Autowired
    public BusinessClientService(BusinessClientRepository businessClientRepository, UpdateUtils<BusinessClient> updateUtils,
                                 PresenceChecker<BusinessClient> presenceChecker) {
        this.businessClientRepository = businessClientRepository;
        this.updateUtils = updateUtils;
        this.presenceChecker = presenceChecker;
    }

    public BusinessClient findById(Long id){
        return businessClientRepository.findById(id).orElse(null);
    }

    public BusinessClient findByEmail(String email) {
        return businessClientRepository.findByEmail(email).orElse(null);
    }

    public BusinessClient findByName(String name) {
        return businessClientRepository.findByName(name).orElse(null);
    }

    public List<BusinessClient> findAll() {
        return businessClientRepository.findAll();
    }

    public List<BusinessClient> findAllPartnershipMembers() {
        return businessClientRepository.findAllByActivePartnershipOffers();
    }

    public List<BusinessClient> findAllNewsletterers() {
        return businessClientRepository.findAllByActiveNewsletter();
    }

    public List<BusinessClient> findAllSubscribers() {
        return businessClientRepository.findAllByActiveNewsletterAndActivePartnershipOffers();
    }

    public BusinessClient save(BusinessClient businessClient) {
        return presenceChecker.checkIfPresent(businessClientRepository, businessClient).orElse(null);
    }

    public boolean update(Long id, BusinessClient newBusinessClientData) {
        return updateUtils.update(businessClientRepository, newBusinessClientData, id);
    }

    public boolean delete(Long id) {
        Optional<BusinessClient> byId = businessClientRepository.findById(id);
        byId.ifPresent(businessClientRepository::delete);
        return byId.isPresent();
    }
}
