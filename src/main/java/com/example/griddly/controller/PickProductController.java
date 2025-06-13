package com.example.griddly.controller;

import com.example.griddly.dto.ProductRequest;
import com.example.griddly.entity.ActionLog;
import com.example.griddly.entity.Product;
import com.example.griddly.entity.StorageSlot;
import com.example.griddly.entity.User;
import com.example.griddly.repository.ActionLogRepository;
import com.example.griddly.repository.StorageSlotRepository;
import com.example.griddly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/products")
public class PickProductController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorageSlotRepository storageSlotRepository;

    @Autowired
    private ActionLogRepository actionLogRepository;

    @PostMapping("/pick")
    public ResponseEntity<?> pickProduct(@RequestBody ProductRequest request) {
        StorageSlot slot = storageSlotRepository
                .findByAisleNumberAndTierNumber(request.getAisle(), request.getTier())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!slot.getIsOccupied()) {
            return ResponseEntity.badRequest().body("Slot is already empty.");
        }

        Product product = slot.getProduct();
        User staff = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        int currentQty = product.getQuantity();
        int qtyToPick = request.getQuantityToPick(); // ✅ match the field name

        if (qtyToPick <= 0 || qtyToPick > currentQty) {
            return ResponseEntity.badRequest().body("Invalid quantity to pick.");
        }

        // Reduce quantity
        product.setQuantity(currentQty - qtyToPick);

        // If no quantity left, clear the slot
        if (product.getQuantity() == 0) {
            slot.setProduct(null);
            slot.setIsOccupied(false);
        }

        // Save updates
        storageSlotRepository.save(slot);

        // Log action
        ActionLog log = ActionLog.builder()
                .user(staff)
                .product(product)
                .action(ActionLog.Action.PICKED)
                .description("Picked " + qtyToPick + " units of " + product.getProductName() +
                        " from Aisle " + request.getAisle() + ", Tier " + request.getTier())
                .build();
        actionLogRepository.save(log);

        return ResponseEntity.ok("Product picked successfully.");
    }

}
