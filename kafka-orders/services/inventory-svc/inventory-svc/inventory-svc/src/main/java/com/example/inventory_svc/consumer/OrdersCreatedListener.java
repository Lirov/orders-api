package com.example.inventory_svc.consumer;

import com.example.inventory_svc.core.StockService;
import com.example.inventory_svc.events.InventoryRejected;
import com.example.inventory_svc.events.InventoryReserved;
import com.example.inventory_svc.events.OrderCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrdersCreatedListener {

    private final StockService stock;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.topics.inventoryReserved}")
    private String reservedTopic;

    @Value("${app.topics.inventoryRejected}")
    private String rejectedTopic;

    @KafkaListener(topics = "${app.topics.ordersCreated}", groupId = "inventory-svc")
    public void onMessage(@Payload OrderCreated evt) {
        log.info("Inventory received: orderId={}, sku={}, qty={}, stock={}",
                evt.getOrderId(), evt.getSku(), evt.getQuantity(), stock.snapshot());

        boolean ok = stock.tryReserve(evt.getSku(), evt.getQuantity());
        if (ok) {
            var out = new InventoryReserved(evt.getOrderId(), evt.getSku(), evt.getQuantity(), Instant.now());
            kafkaTemplate.send(reservedTopic, evt.getOrderId(), out);
            log.info("Reserved inventory → {} x{} for order {}", evt.getSku(), evt.getQuantity(), evt.getOrderId());
        } else {
            var out = new InventoryRejected(evt.getOrderId(), evt.getSku(), evt.getQuantity(),
                    "INSUFFICIENT_STOCK", Instant.now());
            kafkaTemplate.send(rejectedTopic, evt.getOrderId(), out);
            log.warn("Rejected inventory → {} x{} for order {} (insufficient)",
                    evt.getSku(), evt.getQuantity(), evt.getOrderId());
        }
    }
}