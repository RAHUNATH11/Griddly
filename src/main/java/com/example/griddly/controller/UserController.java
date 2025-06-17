//package com.example.griddly.controller;
//
//import com.example.griddly.dto.RegisterRequest;
//import com.example.griddly.entity.User;
//import com.example.griddly.entity.Warehouse;
//import com.example.griddly.repository.UserRepository;
//import com.example.griddly.repository.WarehouseRepository;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@Controller
//public class UserController {
//
//    @Autowired
//    private UserRepository userRepo;
//
//    private final WarehouseRepository warehouseRepo;
//
//    public UserController(WarehouseRepository warehouseRepo) {
//        this.warehouseRepo = warehouseRepo;
//    }
//
//    // ----------------- REGISTER -----------------
//
//    @GetMapping("/register")
//    public String showRegister(Model model) {
//        model.addAttribute("user", new User());
//        return "register"; // fallback for form-based views
//    }
//
//    @PostMapping("/register")
//    @ResponseBody
//    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
//        System.out.println("Received warehouseId: " + request.getWarehouseId());
//
//        // Email uniqueness check
//        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
//            return ResponseEntity.badRequest().body("Email already registered!");
//        }
//
//        Warehouse warehouse = null;
//
//        // Role-specific logic
//        String role = request.getRole().toUpperCase();
//        if ("STAFF".equals(role)) {
//            if (request.getWarehouseId() == null) {
//                return ResponseEntity.badRequest().body("Warehouse ID is required for STAFF role");
//            }
//
//            warehouse = warehouseRepo.findById(request.getWarehouseId())
//                    .orElseThrow(() -> new RuntimeException("Invalid warehouse ID: " + request.getWarehouseId()));
//        }
//        System.out.println("✅ Warehouse fetched: " + warehouse);
//
//        User user = User.builder()
//                .userName(request.getUserName())
//                .email(request.getEmail())
//                .password(request.getPassword()) // Hash in production
//                .role(User.Role.valueOf(role))
//                .warehouse(warehouse)
//                .build();
//
//        userRepo.save(user);
//        return ResponseEntity.ok("Registered");
//    }
//
//    // ----------------- LOGIN -----------------
//
//    @GetMapping("/login")
//    public String showLogin(Model model) {
//        model.addAttribute("user", new User());
//        return "login";
//    }
//
//    @PostMapping("/login")
//    public String processLogin(@ModelAttribute User user, Model model, HttpSession session) {
//        Optional<User> existingUser = userRepo.findByEmail(user.getEmail());
//
//        if (existingUser.isPresent()) {
//            User dbUser = existingUser.get();
//            boolean isPasswordMatch = dbUser.getPassword().equals(user.getPassword());
//            boolean isRoleMatch = dbUser.getRole() == user.getRole();
//
//            if (isPasswordMatch && isRoleMatch) {
//                session.setAttribute("user", dbUser);
//
//                if (dbUser.getRole() == User.Role.ADMIN) {
//                    return "admin_home";
//                } else if (dbUser.getRole() == User.Role.STAFF) {
//                    return "staff_home";
//                }
//            }
//        }
//
//        model.addAttribute("error", "Invalid email, password, or role");
//        return "login";
//    }
//
//    // ----------------- LOGOUT -----------------
//
//    @GetMapping("/logout")
//    public String logout(HttpSession session) {
//        session.invalidate();
//        return "redirect:/login";
//    }
//}
