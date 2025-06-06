package com.example.griddly.controller;

import com.example.griddly.entity.Warehouse;
import com.example.griddly.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouses")
@CrossOrigin(origins = "http://localhost:5173")
public class WarehouseRestController {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @GetMapping("/latest")
    public Warehouse getLatestWarehouse() {
        return warehouseRepository.findTopByOrderByWarehouseIdDesc();
    }

    @PostMapping("/create")
    public Warehouse createWarehouse(@RequestBody Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }
}
