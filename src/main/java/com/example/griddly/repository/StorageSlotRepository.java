package com.example.griddly.repository;

import com.example.griddly.entity.StorageSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StorageSlotRepository extends JpaRepository<StorageSlot, Long> {

    List<StorageSlot> findByWarehouseWarehouseId(Long warehouseId);

    List<StorageSlot> findByIsOccupiedFalseAndWarehouseWarehouseId(Long warehouseId);

    Optional<StorageSlot> findByAisleNumberAndTierNumberAndWarehouseWarehouseId(
            int aisleNumber,
            int tierNumber,
            Long warehouseId
    );
    List<StorageSlot> findByWarehouse_WarehouseId(Long warehouseId);

}
