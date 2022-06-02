package com.so5.api.service;

import com.so5.api.entity.Customer;
import com.so5.api.repository.CreditCardDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreditCardDataServiceTest {

    @Mock
    private CreditCardDataRepository creditCardDataRepository;

    @InjectMocks
    private CreditCardDataService creditCardDataService;

    @Test
    public void whenFindByCustomer_shouldCallRepository() {

        var customer = new Customer();
        creditCardDataService.findByCustomer(customer);

        Mockito.verify(creditCardDataRepository).findByCustomer(customer);
    }
}
