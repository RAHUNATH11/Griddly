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
import java.util.Optional;

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

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody ProductRequest request) {
        if (request.getUserId() == null) {
            return ResponseEntity.badRequest().body("User ID is required");
        }

        Optional<User> staffOpt = userRepository.findById(request.getUserId());
        if (staffOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User staff = staffOpt.get();

        if (staff.getWarehouse() == null) {
            return ResponseEntity.badRequest().body("Staff is not assigned to any warehouse");
        }

        // Find slot in the same warehouse
        Optional<StorageSlot> slotOpt = storageSlotRepository
                .findByAisleNumberAndTierNumberAndWarehouseWarehouseId(
                        request.getAisle(),
                        request.getTier(),
                        staff.getWarehouse().getWarehouseId()
                );

        if (slotOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Storage slot not found in your warehouse");
        }

        StorageSlot slot = slotOpt.get();

        if (slot.getIsOccupied()) {
            return ResponseEntity.status(400).body("Slot is already occupied");
        }

        // Create and save product with warehouse info
        Product product = Product.builder()
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .addedBy(staff)
                .warehouse(staff.getWarehouse())
                .build();
        productRepository.save(product);

        // Assign product to slot
        slot.setProduct(product);
        slot.setIsOccupied(true);
        storageSlotRepository.save(slot);

        // Log the action with warehouse info
        ActionLog log = ActionLog.builder()
                .user(staff)
                .product(product)
                .warehouse(staff.getWarehouse())
                .action(ActionLog.Action.ADDED)
                .description("Added to aisle " + request.getAisle() + ", tier " + request.getTier())
                .build();
        actionLogRepository.save(log);

        return ResponseEntity.ok("Product added.");
    }

    @PostMapping("/pick")
    public ResponseEntity<?> pickProduct(@RequestBody ProductRequest request) {
        if (request.getUserId() == null) {
            return ResponseEntity.badRequest().body("User ID is required");
        }

        if (request.getQuantity() == 0 || request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("Pick quantity must be greater than 0");
        }

        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User staff = userOpt.get();

        if (staff.getWarehouse() == null) {
            return ResponseEntity.badRequest().body("Staff is not assigned to any warehouse");
        }

        Long warehouseId = staff.getWarehouse().getWarehouseId();

        Optional<StorageSlot> slotOpt = storageSlotRepository
                .findByAisleNumberAndTierNumberAndWarehouseWarehouseId(
                        request.getAisle(),
                        request.getTier(),
                        warehouseId
                );

        if (slotOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Slot not found in your warehouse");
        }

        StorageSlot slot = slotOpt.get();

        if (!slot.getIsOccupied() || slot.getProduct() == null) {
            return ResponseEntity.badRequest().body("Slot is empty");
        }

        Product product = slot.getProduct();
        int currentQty = product.getQuantity();
        int pickQty = request.getQuantity();

        if (pickQty > currentQty) {
            return ResponseEntity.badRequest().body("Pick quantity exceeds available quantity");
        }

        String pickedProductName = product.getProductName(); // Save for logging

        if (pickQty == currentQty) {
            // Set quantity to 0 and remove it from the slot, but do not delete
            product.setQuantity(0);
            slot.setProduct(null);
            slot.setIsOccupied(false);
            productRepository.save(product);
            storageSlotRepository.save(slot);
        } else {
            product.setQuantity(currentQty - pickQty);
            productRepository.save(product);
        }

        ActionLog log = ActionLog.builder()
                .user(staff)
                .warehouse(staff.getWarehouse())
                .product(product) // Still safe to log product even if quantity is 0
                .action(ActionLog.Action.PICKED)
                .description("Picked " + pickQty + " of '" + pickedProductName +
                        "' from Aisle " + request.getAisle() + ", Tier " + request.getTier())
                .build();
        actionLogRepository.save(log);

        return ResponseEntity.ok("Product picked successfully");
    }


    @GetMapping("/slots")
    public ResponseEntity<?> getSlots(@RequestParam Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();
        if (user.getWarehouse() == null) {
            return ResponseEntity.badRequest().body("User not assigned to warehouse");
        }

        Long warehouseId = user.getWarehouse().getWarehouseId();
        List<StorageSlot> slots = storageSlotRepository.findByWarehouseWarehouseId(warehouseId);

        return ResponseEntity.ok(slots);
    }
    @GetMapping("/slots/{warehouseId}")
    public List<StorageSlot> getSlotsByWarehouse(@PathVariable Long warehouseId) {
        return storageSlotRepository.findByWarehouse_WarehouseId(warehouseId);
    }


}
