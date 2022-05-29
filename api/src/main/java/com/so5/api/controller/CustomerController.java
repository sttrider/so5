package com.so5.api.controller;

import com.so5.api.entity.Customer;
import com.so5.api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/")
    @Secured({"ROLE_customer"})
    public ResponseEntity<Void> save(@RequestBody Customer customerSave, UriComponentsBuilder uriComponentsBuilder) {

        var customer = customerService.save(customerSave);

        var uriComponents =
                uriComponentsBuilder.path("/customer/{id}").buildAndExpand(customer.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_customer"})
    public Customer findById(@PathVariable("id") Long id) {
        return customerService.findById(id);
    }
}
