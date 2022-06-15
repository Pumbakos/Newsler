package pl.palubiak.dawid.newsler.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.businesclinet.service.BusinessClientService;
import pl.palubiak.dawid.newsler.generators.BusinessClientGenerator;

import java.util.List;

@ContextConfiguration(classes = {BusinessClientService.class})
@SpringBootTest
class BusinessClientServiceTest {
    @MockBean
    private BusinessClientService businessClientService;

    @Test
    @DisplayName("Should return business client by id")
    void findBusinessClientById() {
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(businessClientService.findById(1L)).thenReturn(businessClient);
        Assertions.assertEquals(businessClient, businessClientService.findById(1L));
    }

    @Test
    @DisplayName("Should return null due to wrong id")
    void notFindBusinessClientById() {
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(businessClientService.findById(1L)).thenReturn(businessClient);
        Assertions.assertEquals(businessClient, businessClientService.findById(1L));
    }


    @Test
    @DisplayName("Should save businessClient and return it's instance")
    void saveValidUser(){
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(businessClientService.save(Mockito.any(BusinessClient.class))).thenReturn(businessClient);
        BusinessClient result = businessClientService.save(businessClient);

        Assertions.assertEquals(businessClient, result);
    }

    @Test
    @DisplayName("Should not save businessClient and return null")
    void saveInvalidUser(){
        BusinessClient invalidBusinessClient = BusinessClientGenerator.createInvalidBusinessClient();
        Mockito.when(businessClientService.save(Mockito.any(BusinessClient.class))).thenReturn(null);
        BusinessClient result = businessClientService.save(invalidBusinessClient);

        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("Should not save present businessClient and return null")
    void savePresentValidUser(){
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(businessClientService.save(Mockito.any(BusinessClient.class))).thenReturn(null);
        BusinessClient result = businessClientService.save(businessClient);

        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("Should update businessClient and return true")
    void updatePresentUserWithValidData(){
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(businessClientService.update(Mockito.anyLong(), Mockito.any(BusinessClient.class))).thenReturn(true);
        boolean result = businessClientService.update(businessClient.getId(), businessClient);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Should not update businessClient and return false")
    void updatePresentUserWithInvalidData(){
        BusinessClient invalidBusinessClient = BusinessClientGenerator.createInvalidBusinessClient();
        Mockito.when(businessClientService.update(Mockito.anyLong(), Mockito.any(BusinessClient.class))).thenReturn(false);
        boolean result = businessClientService.update(invalidBusinessClient.getId(), invalidBusinessClient);

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Should not update businessClient and return false")
    void updateNotPresentUserWithValidData(){
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(businessClientService.update(Mockito.anyLong(), Mockito.any(BusinessClient.class))).thenReturn(false);
        boolean result = businessClientService.update(businessClient.getId(), businessClient);

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Should not update businessClient and return false")
    void updateNotPresentUserWithInvalidData(){
        BusinessClient invalidBusinessClient = BusinessClientGenerator.createInvalidBusinessClient();
        Mockito.when(businessClientService.update(Mockito.anyLong(), Mockito.any(BusinessClient.class))).thenReturn(false);
        boolean result = businessClientService.update(invalidBusinessClient.getId(), invalidBusinessClient);

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Should delete businessClient and return true")
    void deletePresentBusinessClient(){
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(businessClientService.delete(Mockito.anyLong())).thenReturn(true);
        boolean result = businessClientService.delete(businessClient.getId());

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Should not delete businessClient and return false")
    void deleteNotPresentBusinessClient(){
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(businessClientService.delete(Mockito.anyLong())).thenReturn(false);
        boolean result = businessClientService.delete(businessClient.getId());

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Should find businessClient by email and return businessClient")
    void findByEmail(){
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(businessClientService.findByEmail(Mockito.anyString())).thenReturn(businessClient);
        BusinessClient result = businessClientService.findByEmail(businessClient.getEmail());

        Assertions.assertEquals(businessClient, result);
    }

    @Test
    @DisplayName("Should not find businessClient by email and return null")
    void notFindByEmail(){
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(businessClientService.findByEmail(Mockito.anyString())).thenReturn(null);
        BusinessClient result = businessClientService.findByEmail(businessClient.getEmail());

        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("Should find businessClient by name and return businessClient")
    void findByName(){
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(businessClientService.findByName(Mockito.anyString())).thenReturn(businessClient);
        BusinessClient result = businessClientService.findByName(businessClient.getName());

        Assertions.assertEquals(businessClient, result);
    }

    @Test
    @DisplayName("Should not find businessClient by name and return null")
    void notFindByName(){
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(businessClientService.findByName(Mockito.anyString())).thenReturn(null);
        BusinessClient result = businessClientService.findByName(businessClient.getName());

        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("Should find all businessClients and return list of businessClients")
    void findAll(){
        List<BusinessClient> businessClientList = BusinessClientGenerator.createBusinessClientList();
        Mockito.when(businessClientService.findAll()).thenReturn(businessClientList);
        List<BusinessClient> result = businessClientService.findAll();

        Assertions.assertEquals(businessClientList, result);
    }

    @Test
    @DisplayName("Should not find all businessClients and return empty list")
    void notFindAll(){
        Mockito.when(businessClientService.findAll()).thenReturn(List.of());
        List<BusinessClient> result = businessClientService.findAll();

        Assertions.assertEquals(List.of(), result);
    }

    @Test
    @DisplayName("Should find all active partnership members and return it's list")
    void findAllActivePartnershipMembers(){
        List<BusinessClient> businessClientList = BusinessClientGenerator.createBusinessClientList();
        Mockito.when(businessClientService.findAllPartnershipMembers()).thenReturn(businessClientList);
        List<BusinessClient> result = businessClientService.findAllPartnershipMembers();

        Assertions.assertEquals(businessClientList, result);
    }

    @Test
    @DisplayName("Should not find all active partnership members and return empty list")
    void notFindAllActivePartnershipMembers(){
        Mockito.when(businessClientService.findAllPartnershipMembers()).thenReturn(List.of());
        List<BusinessClient> result = businessClientService.findAllPartnershipMembers();

        Assertions.assertEquals(List.of(), result);
    }

    @Test
    @DisplayName("Should find all active newsletterers and return it's list")
    void findAllActiveNewsletterers(){
        List<BusinessClient> businessClientList = BusinessClientGenerator.createBusinessClientList();
        Mockito.when(businessClientService.findAllNewsletterers()).thenReturn(businessClientList);
        List<BusinessClient> result = businessClientService.findAllNewsletterers();

        Assertions.assertEquals(businessClientList, result);
    }

    @Test
    @DisplayName("Should not find all active newsletterers and return empty list")
    void notFindAllActiveNewsletterers(){
        Mockito.when(businessClientService.findAllNewsletterers()).thenReturn(List.of());
        List<BusinessClient> result = businessClientService.findAllNewsletterers();

        Assertions.assertEquals(List.of(), result);
    }

    @Test
    @DisplayName("Should find all active subscribers and return it's list")
    void findAllSubscribers(){
        List<BusinessClient> businessClientList = BusinessClientGenerator.createBusinessClientList();
        Mockito.when(businessClientService.findAllSubscribers()).thenReturn(businessClientList);
        List<BusinessClient> result = businessClientService.findAllSubscribers();

        Assertions.assertEquals(businessClientList, result);
    }

    @Test
    @DisplayName("Should not find all active subscribers and return empty list")
    void notFindAllSubscribers(){
        Mockito.when(businessClientService.findAllSubscribers()).thenReturn(List.of());
        List<BusinessClient> result = businessClientService.findAllSubscribers();

        Assertions.assertEquals(List.of(), result);
    }
}
