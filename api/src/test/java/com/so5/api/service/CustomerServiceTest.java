package com.so5.api.service;

import com.so5.api.entity.Customer;
import com.so5.api.exception.EntityNotFoundException;
import com.so5.api.repository.CustomerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    public void whenFindByEmailWithExistingEmail_thenShouldReturnCustomer() {

        var email = "email@email.com";

        Mockito.when(customerRepository.findByEmail(email)).thenReturn(Optional.of(new Customer()));

        var customer = customerService.findByEmail(email);

        Mockito.verify(customerRepository).findByEmail(email);
        Assertions.assertThat(customer).isNotNull();
    }

    @Test
    public void whenFindByEmailNonWithExistingEmail_thenShouldThrowEntityNotFoundException() {

        var email = "email@email.com";

        Mockito.when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () -> customerService.findByEmail(email));

        Assertions.assertThat(exception.getReason()).isEqualTo("Not found.");
        Assertions.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        Mockito.verify(customerRepository).findByEmail(email);
    }
}
