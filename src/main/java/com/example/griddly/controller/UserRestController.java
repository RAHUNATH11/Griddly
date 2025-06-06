package com.example.griddly.controller;

import com.example.griddly.entity.User;
import com.example.griddly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173") // Vite runs on port 5173 by default
public class UserRestController {

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        Optional<User> existingUser = userRepo.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");
        }
        return ResponseEntity.ok(userRepo.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> existingUser = userRepo.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();

            boolean isPasswordMatch = dbUser.getPassword().equals(user.getPassword());
            boolean isRoleMatch = dbUser.getRole() == user.getRole(); // match role from request

            if (isPasswordMatch && isRoleMatch) {
                // ✅ Return role-based info
                Map<String, Object> response = new HashMap<>();
                response.put("userName", dbUser.getUserName());
                response.put("email", dbUser.getEmail());
                response.put("role", dbUser.getRole());
                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email, password, or role");
    }

}
