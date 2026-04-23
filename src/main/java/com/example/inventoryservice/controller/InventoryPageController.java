package com.example.inventoryservice.controller;

import com.example.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class InventoryPageController {

    private final InventoryService inventoryService;

    @GetMapping({"/", "/inventory"})
    public String inventoryPage(Model model) {
        model.addAttribute("items", inventoryService.getAllInventories());
        return "inventory";
    }
}
