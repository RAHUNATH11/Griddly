package com.example.griddly.controller;

import com.example.griddly.entity.StorageSlot;
import com.example.griddly.entity.Warehouse;
import com.example.griddly.repository.StorageSlotRepository;
import com.example.griddly.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouses")
@CrossOrigin(origins = "http://localhost:5173")
public class WarehouseRestController {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private StorageSlotRepository storageSlotRepository;

    @GetMapping("/latest")
    public Warehouse getLatestWarehouse() {
        //System.out.println(warehouseRepository.findAll());
        return warehouseRepository.findTopByOrderByWarehouseIdDesc();
    }

    @PostMapping("/create")
    public Warehouse createWarehouse(@RequestBody Warehouse warehouse) {
        // Save warehouse
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        // Auto-generate slots
        for (int aisle = 1; aisle <= warehouse.getNoOfAisles(); aisle++) {
            for (int tier = 1; tier <= warehouse.getNoOfTiers(); tier++) {
                StorageSlot slot = StorageSlot.builder()
                        .warehouse(savedWarehouse)
                        .aisleNumber(aisle)
                        .tierNumber(tier)
                        .isOccupied(false)
                        .product(null)
                        .build();
                storageSlotRepository.save(slot);
            }
        }

        return savedWarehouse;
    }
    @GetMapping("/report")
    public Map<String, Object> getSlotReport() {
        List<StorageSlot> allSlots = storageSlotRepository.findAll();

        long occupied = allSlots.stream().filter(StorageSlot::getIsOccupied).count();
        long available = allSlots.size() - occupied;

        Map<String, Object> report = new HashMap<>();
        report.put("totalSlots", allSlots.size());
        report.put("occupiedSlots", occupied);
        report.put("availableSlots", available);
        report.put("totalAisles", allSlots.stream().map(StorageSlot::getAisleNumber).distinct().count());
        report.put("totalTiers", allSlots.stream().map(StorageSlot::getTierNumber).distinct().count());

        // Slot details with product info
        List<Map<String, Object>> details = allSlots.stream().map(slot -> {
            Map<String, Object> s = new HashMap<>();
            s.put("aisleNumber", slot.getAisleNumber());
            s.put("tierNumber", slot.getTierNumber());
            s.put("occupied", slot.getIsOccupied());
            if (slot.getProduct() != null) {
                s.put("product", Map.of(
                        "productName", slot.getProduct().getProductName(),
                        "quantity", slot.getProduct().getQuantity()
                ));
            } else {
                s.put("product", null);
            }
            return s;
        }).toList();

        report.put("details", details);
        return report;
    }
//
////
//    @PostMapping("/create")
//    public Warehouse createWarehouse(@RequestBody Warehouse warehouse) {
//        return warehouseRepository.save(warehouse);
//    }
}
