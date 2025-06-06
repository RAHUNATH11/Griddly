package com.example.griddly.controller;
import com.example.griddly.dto.PickRequest;
import com.example.griddly.entity.ActionLog;
import com.example.griddly.entity.Product;
import com.example.griddly.entity.StorageSlot;
import com.example.griddly.repository.ActionLogRepository;
import com.example.griddly.repository.StorageSlotRepository;
import com.example.griddly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PickProductController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StorageSlotRepository storageSlotRepository;
    @Autowired
    private ActionLogRepository actionLogRepository;
    @PostMapping("/pick")
    public ResponseEntity<?> pickProduct(@RequestBody PickRequest request) {
        StorageSlot slot = storageSlotRepository
                .findByAisleNumberAndTierNumber(request.getAisle(), request.getTier())
                .orElseThrow(() -> new RuntimeException("Slot not found"));
        if (!slot.getIsOccupied()) return ResponseEntity.badRequest().body("Slot empty");

        Product product = slot.getProduct();
        slot.setProduct(null);
        slot.setIsOccupied(false);
        storageSlotRepository.save(slot);

        ActionLog log = ActionLog.builder()
                .user(userRepository.findById(request.getUserId()).orElseThrow())
                .product(product)
                .action(ActionLog.Action.PICKED)
                .description("Picked from aisle " + request.getAisle() + ", tier " + request.getTier())
                .build();
        actionLogRepository.save(log);

        return ResponseEntity.ok("Product picked.");
    }
    @PostMapping("/pick/{slotId}")
    public ResponseEntity<?> pickProduct(@PathVariable Long slotId) {
        StorageSlot slot = storageSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!slot.getIsOccupied()) {
            return ResponseEntity.badRequest().body("Slot is already empty.");
        }

        Product product = slot.getProduct();

        // Optionally, delete or mark product as picked
        // productRepository.delete(product);

        slot.setProduct(null);
        slot.setIsOccupied(false);
        storageSlotRepository.save(slot);

        return ResponseEntity.ok("Product picked successfully.");
    }

}
