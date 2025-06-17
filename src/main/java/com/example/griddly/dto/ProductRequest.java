package com.example.griddly.dto;

import lombok.Data;

@Data
public class ProductRequest {
    private String productName;
    private Integer quantity; // 🔁 change from int to Integer
    private Integer aisle;
    private Integer tier;
    private Long userId;

    public int getQuantity() {
        return quantity != null ? quantity : 0;
    }
}
