package com.example.griddly.controller;

import com.example.griddly.dto.ProductRequest;
import com.example.griddly.entity.ActionLog;
import com.example.griddly.entity.Product;
import com.example.griddly.entity.StorageSlot;
import com.example.griddly.entity.User;
import com.example.griddly.repository.ActionLogRepository;
import com.example.griddly.repository.ProductRepository;
import com.example.griddly.repository.StorageSlotRepository;
import com.example.griddly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductRestController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StorageSlotRepository storageSlotRepository;
    @Autowired
    private ActionLogRepository actionLogRepository;
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
//@GetMapping("/ping")
//public String ping() {
//    return "Backend working ";
//}

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody ProductRequest request) {
        User staff = userRepository.findById(request.getUserId()).orElseThrow();
        Product product = Product.builder()
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .addedBy(staff)
                .build();
        productRepository.save(product);
        System.out.println(storageSlotRepository
                .findByAisleNumberAndTierNumber(request.getAisle(), request.getTier()));
        System.out.println("Looking for slot at aisle " + request.getAisle() + ", tier " + request.getTier());
        StorageSlot slot = storageSlotRepository
                .findByAisleNumberAndTierNumber(request.getAisle(), request.getTier())
                .orElseThrow(() -> new RuntimeException("Slot not found"));
        slot.setProduct(product);
        slot.setIsOccupied(true);
        storageSlotRepository.save(slot);

        ActionLog log = ActionLog.builder()
                .user(staff)
                .product(product)
                .action(ActionLog.Action.ADDED)
                .description("Added to aisle " + request.getAisle() + ", tier " + request.getTier())
                .build();
        actionLogRepository.save(log);

        return ResponseEntity.ok("Product added.");
    }
    @GetMapping("/slots")
    public List<StorageSlot> getAllSlots() {
        return storageSlotRepository.findAll();
    }


}

