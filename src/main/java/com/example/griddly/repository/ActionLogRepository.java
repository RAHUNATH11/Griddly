package com.example.griddly.repository;

import com.example.griddly.entity.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {

    List<ActionLog> findByWarehouse_WarehouseId(Long warehouseId);

}
