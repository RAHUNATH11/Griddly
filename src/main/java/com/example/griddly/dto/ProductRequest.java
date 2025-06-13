package com.example.griddly.dto;

import lombok.Data;

@Data
public class ProductRequest {
    private String productName;
    private int quantityToPick; // 👈 updated field name
    private int aisle;
    private int tier;
    private Long userId;


    public int getQuantity() {
        return quantityToPick;
    }

//    public void setQuantity(int quantity) {
//        this.quantityToPick = quantity;
//    }
}
