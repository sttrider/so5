package com.so5.api.service;

import com.so5.api.entity.Customer;
import com.so5.api.exception.EntityNotFoundException;
import com.so5.api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Customer save(Customer customer) {
        customer.setCreationDate(LocalDateTime.now());
        return customerRepository.save(customer);
    }
}
