package com.example.order_api.consumer;

import com.example.order_api.core.OrderStore;
import com.example.order_api.events.InventoryRejected;
import com.example.order_api.events.InventoryReserved;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.example.order_api.core.OrderStore.Status.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventsListener {

    private final OrderStore store;

    @KafkaListener(topics = "${app.topics.inventoryReserved}", groupId = "order-api")
    public void onReserved(@Payload InventoryReserved evt) {
        log.info("Order {} reserved for sku {} x{}", evt.getOrderId(), evt.getSku(), evt.getQuantity());
        store.put(evt.getOrderId(), RESERVED);
    }

    @KafkaListener(topics = "${app.topics.inventoryRejected}", groupId = "order-api")
    public void onRejected(@Payload InventoryRejected evt) {
        log.warn("Order {} rejected for sku {} (reason={})", evt.getOrderId(), evt.getSku(), evt.getReason());
        store.put(evt.getOrderId(), REJECTED);
    }
}
