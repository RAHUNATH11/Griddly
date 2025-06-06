package com.example.griddly.dto;

import lombok.Data;

@Data
public class ProductRequest {
    private String productName;
    private int quantity;
    private Long userId;
    private int aisle;
    private int tier;
}

