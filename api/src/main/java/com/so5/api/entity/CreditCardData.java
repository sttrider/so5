package com.so5.api.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_card_data")
@Getter
@Setter
public class CreditCardData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    private String holder;
    private String number;
    @Column(name = "verification_code")
    private String verificationCode;
    @Column(name = "expiration_date")
    private LocalDate expirationDate;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
}
