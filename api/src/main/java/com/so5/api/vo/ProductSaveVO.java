package com.so5.api.vo;

import lombok.Data;

@Data
public class ProductSaveVO {

    private String sku;
    private String name;
    private String description;
    private Double price;
    private Integer inventory;
    private Integer shipmentDeliveryTimes;
    private boolean enabled;
    private Long categoryId;
    private String image;
}
