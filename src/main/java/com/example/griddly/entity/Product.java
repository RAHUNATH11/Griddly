package com.example.griddly.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;

    private int quantity;

    private LocalDateTime dateAdded;

    @ManyToOne
    @JoinColumn(name = "added_by")
    private User addedBy;

    @PrePersist
    public void prePersist() {
        dateAdded = LocalDateTime.now();
    }
}
