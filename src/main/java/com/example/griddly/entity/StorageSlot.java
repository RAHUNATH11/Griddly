package com.example.griddly.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "storage_slot")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorageSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    private int aisleNumber;

    private int tierNumber;

    private boolean isOccupied;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    public boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(boolean occupied) {
        this.isOccupied = occupied;
    }

}
