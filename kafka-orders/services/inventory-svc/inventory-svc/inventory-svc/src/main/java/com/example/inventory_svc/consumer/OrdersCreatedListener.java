package com.example.inventory_svc.consumer;

import com.example.inventory_svc.events.OrderCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrdersCreatedListener {

    @KafkaListener(topics = "${app.topics.ordersCreated}", groupId = "inventory-svc")
    public void onMessage(@Payload OrderCreated evt) {
        log.info("Inventory received: orderId={}, sku={}, qty={}",
                evt.getOrderId(), evt.getSku(), evt.getQuantity());
        // next step: reserve stock and publish inventory.reserved
    }
}
