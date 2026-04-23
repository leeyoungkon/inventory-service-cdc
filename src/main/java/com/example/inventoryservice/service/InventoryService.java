package com.example.inventoryservice.service;

import com.example.inventoryservice.domain.Inventory;
import com.example.inventoryservice.domain.InventoryRepository;
import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public void decreaseStock(OrderCreatedEvent event) {
        Inventory inventory = inventoryRepository.findById(event.getProductName())
                .orElseThrow(() -> new IllegalArgumentException("inventory not found for product=" + event.getProductName()));

        inventory.decrease(event.getQuantity());
        inventoryRepository.save(inventory);

        log.info("stock decreased. orderNo={}, productName={}, quantity={}, remaining={}",
                event.getOrderNo(), event.getProductName(), event.getQuantity(), inventory.getStock());
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventories() {
        return inventoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(String productName) {
        Inventory inventory = inventoryRepository.findById(productName)
                .orElseThrow(() -> new IllegalArgumentException("inventory not found for product=" + productName));
        return toResponse(inventory);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .productName(inventory.getProductName())
                .stock(inventory.getStock())
                .updatedAt(inventory.getUpdatedAt().toString())
                .build();
    }
}
