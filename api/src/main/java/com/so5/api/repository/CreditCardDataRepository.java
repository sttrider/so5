package com.so5.api.repository;

import com.so5.api.entity.CreditCardData;
import com.so5.api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditCardDataRepository extends JpaRepository<CreditCardData, Long> {

    Optional<CreditCardData> findByCustomer(Customer customer);
}
