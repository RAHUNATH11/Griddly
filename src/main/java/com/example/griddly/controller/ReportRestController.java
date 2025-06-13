package com.example.griddly.controller;

import com.example.griddly.entity.StorageSlot;
import com.example.griddly.repository.StorageSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportRestController {

    @Autowired
    private StorageSlotRepository storageSlotRepository;

    @GetMapping("/slot-summary")
    public Map<String, Object> getSlotSummary() {
        List<StorageSlot> allSlots = storageSlotRepository.findAll();

        long total = allSlots.size();
        long occupied = allSlots.stream().filter(StorageSlot::getIsOccupied).count();
        long available = total - occupied;

        List<Map<String, Object>> breakdown = new ArrayList<>();
        for (StorageSlot slot : allSlots) {
            Map<String, Object> slotMap = new HashMap<>();
            slotMap.put("aisle", slot.getAisleNumber());
            slotMap.put("tier", slot.getTierNumber());
            slotMap.put("occupied", slot.getIsOccupied());

            if (slot.getProduct() != null) {
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("productName", slot.getProduct().getProductName());
                productMap.put("quantity", slot.getProduct().getQuantity());
                slotMap.put("product", productMap);
            } else {
                slotMap.put("product", null);
            }

            breakdown.add(slotMap);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalSlots", total);
        response.put("occupiedSlots", occupied);
        response.put("availableSlots", available);
        response.put("slotBreakdown", breakdown);

        return response;
    }
}
