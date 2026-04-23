package com.example.inventoryservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;

    @Column(nullable = false)
    private int stock;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void decrease(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        if (stock < quantity) {
            throw new IllegalStateException(
                    "insufficient stock for product=" + productName + ", current=" + stock + ", requested=" + quantity
            );
        }
        this.stock -= quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void increase(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        this.stock += quantity;
        this.updatedAt = LocalDateTime.now();
    }
}
