package com.so5.api.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sku;
    private String name;
    private String description;
    private Double price;
    private String image;
    private Integer inventory;
    @Column(name = "shipment_delivery_times")
    private Integer shipmentDeliveryTimes;
    private boolean enabled;
    @ManyToOne()
    @JoinColumn(name = "category_id")
    private ProductCategory category;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
}
