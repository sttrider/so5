package com.so5.api.service;

import com.so5.api.entity.CreditCardData;
import com.so5.api.entity.Customer;
import com.so5.api.repository.CreditCardDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditCardDataService {

    private final CreditCardDataRepository creditCardDataRepository;

    public Optional<CreditCardData> findByCustomer(Customer customer) {
        return creditCardDataRepository.findByCustomer(customer);
    }
}
