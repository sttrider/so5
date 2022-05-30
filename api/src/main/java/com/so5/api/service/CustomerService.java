package com.so5.api.service;

import com.so5.api.entity.Customer;
import com.so5.api.exception.EntityNotFoundException;
import com.so5.api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
    }

}
