package com.so5.api.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String sku;
    private String name;
    private String description;
    private Double price;
    private String image;
    private Integer inventory;
    @Column(name = "shipment_delivery_times")
    private Integer shipmentDeliveryTimes;
    private boolean enabled;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ProductCategory category;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
}
