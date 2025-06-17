package com.example.griddly.controller;

import com.example.griddly.entity.StorageSlot;
import com.example.griddly.entity.Warehouse;
import com.example.griddly.repository.StorageSlotRepository;
import com.example.griddly.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public List<Map<String, Object>> getReportsByWarehouse() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<Map<String, Object>> reports = new ArrayList<>();

        for (Warehouse warehouse : warehouses) {
            List<StorageSlot> slots = storageSlotRepository.findByWarehouse_WarehouseId(warehouse.getWarehouseId());

            long occupied = slots.stream().filter(StorageSlot::getIsOccupied).count();
            long available = slots.size() - occupied;

            Map<String, Object> report = new HashMap<>();
            report.put("warehouseId", warehouse.getWarehouseId());
            report.put("warehouseName", warehouse.getWarehouseName());
            report.put("totalSlots", slots.size());
            report.put("occupiedSlots", occupied);
            report.put("availableSlots", available);
            report.put("totalAisles", slots.stream().map(StorageSlot::getAisleNumber).distinct().count());
            report.put("totalTiers", slots.stream().map(StorageSlot::getTierNumber).distinct().count());

            reports.add(report);
        }

        return reports;
    }
    @GetMapping("/report/{warehouseId}")
    public Map<String, Object> getSlotReportByWarehouse(@PathVariable Long warehouseId) {
        List<StorageSlot> slots = storageSlotRepository.findByWarehouse_WarehouseId(warehouseId);

        long occupied = slots.stream().filter(StorageSlot::getIsOccupied).count();
        long available = slots.size() - occupied;

        Map<String, Object> report = new HashMap<>();
        report.put("totalSlots", slots.size());
        report.put("occupiedSlots", occupied);
        report.put("availableSlots", available);
        return report;
    }


    @GetMapping("/all")
    public List<Warehouse> getAll() {
        return warehouseRepository.findAll();
    }
    @GetMapping("/{id}")
    public Warehouse getWarehouseById(@PathVariable Long id) {
        return warehouseRepository.findById(id).orElse(null);
    }


//
////
//    @PostMapping("/create")
//    public Warehouse createWarehouse(@RequestBody Warehouse warehouse) {
//        return warehouseRepository.save(warehouse);
//    }
}
