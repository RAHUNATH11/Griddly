package com.example.griddly.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String userName;
    private String email;
    private String password;
    private String role; // "STAFF" or "ADMIN"
    private Long warehouseId; // Nullable for admin
}
