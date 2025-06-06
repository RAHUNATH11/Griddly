package com.example.griddly.dto;

import lombok.Data;

@Data
public class PickRequest {
    private int aisle;
    private int tier;
    private Long userId;
}

