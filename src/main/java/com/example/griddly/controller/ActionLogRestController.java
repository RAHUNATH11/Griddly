package com.example.griddly.controller;

import com.example.griddly.entity.ActionLog;
import com.example.griddly.repository.ActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "http://localhost:5173")
public class ActionLogRestController {

    @Autowired
    private ActionLogRepository actionLogRepository;

//    @GetMapping
//    public List<ActionLog> getAllLogs() {
//        return actionLogRepository.findAll();
//    }
    @GetMapping("/byWarehouse/{warehouseId}")
    public List<ActionLog> getLogsByWarehouse(@PathVariable Long warehouseId) {
        return actionLogRepository.findByWarehouse_WarehouseId(warehouseId);
    }

}
 