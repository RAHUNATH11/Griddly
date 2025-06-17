package com.example.griddly.controller;

import com.example.griddly.dto.RegisterRequest; // Import the RegisterRequest DTO
import com.example.griddly.entity.User;
import com.example.griddly.entity.Warehouse; // Import Warehouse entity
import com.example.griddly.repository.UserRepository;
import com.example.griddly.repository.WarehouseRepository; // Import WarehouseRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173") // Vite runs on port 5173 by default
public class UserRestController {

    // Using SLF4J Logger is a better practice than System.out.println in Spring
    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired // Auto-wire WarehouseRepository
    private WarehouseRepository warehouseRepo;



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Check for existing user by email
        Optional<User> existingUser = userRepo.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");
        }

        // Convert role string to enum
        User.Role userRole;
        try {
            userRole = User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role specified.");
        }

        // Validate warehouse for STAFF role
        Warehouse warehouse = null;
        if (userRole == User.Role.STAFF) {
            if (request.getWarehouseId() == null) {
                return ResponseEntity.badRequest().body("Warehouse ID is required for STAFF role");
            }

            warehouse = warehouseRepo.findById(request.getWarehouseId())
                    .orElse(null);

            if (warehouse == null) {
                return ResponseEntity.badRequest().body("Invalid warehouse ID");
            }
        }

        // Build and save user
        User newUser = User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(userRole)
                .warehouse(warehouse)
                .build();

        userRepo.save(newUser);

        return ResponseEntity.ok("Registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        Optional<User> existingUser = userRepo.findByEmail(email);
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();
            boolean isPasswordMatch = dbUser.getPassword().equals(password);

            if (isPasswordMatch) {
                Map<String, Object> response = new HashMap<>();
                response.put("userId", dbUser.getUserId());
                response.put("userName", dbUser.getUserName());
                response.put("email", dbUser.getEmail());

                if (dbUser.getWarehouse() == null) {
                    response.put("role", "ADMIN");
                } else {
                    response.put("role", "STAFF");
                    response.put("warehouseId", dbUser.getWarehouse().getWarehouseId());
                    response.put("warehouseName", dbUser.getWarehouse().getWarehouseName());
                }

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }


}
