package com.example.inventoryservice.messaging;

import com.example.inventoryservice.dto.OrderCreatedEvent;
import com.example.inventoryservice.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    @KafkaListener(topics = "${app.kafka.topic.order-created:order.created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        try {
            log.info("received Kafka message={}", message);
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            inventoryService.decreaseStock(event);
        } catch (Exception e) {
            log.error("inventory processing failed for message={}", message, e);
            throw new RuntimeException(e);
        }
    }
}
