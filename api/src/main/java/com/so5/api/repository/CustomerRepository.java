package com.so5.api.repository;

import com.so5.api.entity.Customer;
import com.so5.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = "SELECT u FROM User u WHERE u.id = :id")
    User findByUserId(@Param("id") Long id);
}
