package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryRestController {

    private final InventoryService inventoryService;

    @GetMapping
    public List<InventoryResponse> getAll() {
        return inventoryService.getAllInventories();
    }

    @GetMapping("/{productName}")
    public InventoryResponse getOne(@PathVariable String productName) {
        return inventoryService.getInventory(productName);
    }
}
