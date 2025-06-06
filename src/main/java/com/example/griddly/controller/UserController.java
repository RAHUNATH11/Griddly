package com.example.griddly.controller;

import com.example.griddly.entity.User;
import com.example.griddly.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepo;

    // Show Register Page
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Handle Register and save user
    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user, Model model) {
        Optional<User> existingUser = userRepo.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        userRepo.save(user);
        return "redirect:/login";
    }

    // Show Login Page
    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    // Handle Login and redirect by role
    @PostMapping("/login")
    public String processLogin(@ModelAttribute User user, Model model, HttpSession session) {
        Optional<User> existingUser = userRepo.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();

            boolean isPasswordMatch = dbUser.getPassword().equals(user.getPassword());
            boolean isRoleMatch = dbUser.getRole() == user.getRole(); // 🔥 compare role

            if (isPasswordMatch && isRoleMatch) {
                session.setAttribute("user", dbUser);

                if (dbUser.getRole() == User.Role.ADMIN) {
                    return "admin_home";
                } else if (dbUser.getRole() == User.Role.STAFF) {
                    return "staff_home";
                }
            }
        }

        model.addAttribute("error", "Invalid email, password, or role");
        return "login";
    }


    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
