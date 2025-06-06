package com.example.griddly.repository;

import com.example.griddly.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Warehouse findTopByOrderByWarehouseIdDesc();
}
