package com.example.order_api.web;

import com.example.order_api.core.OrderStore;
import com.example.order_api.events.OrderCreated;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/orders")
@Validated
@RequiredArgsConstructor
public class OrderController {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderStore store;

    @Value("${app.topics.ordersCreated:orders.created}")
    private String ordersTopic;

    // Simple in-memory store for demo purposes only
    private final Map<String, OrderCreated> orderStore = new ConcurrentHashMap<>();

    @PostMapping
    public CreateOrderResponse create(@RequestBody @jakarta.validation.Valid CreateOrderRequest req) {
        String orderId = UUID.randomUUID().toString();
        OrderCreated evt = new OrderCreated(orderId, req.sku(), req.quantity(), Instant.now());

        kafkaTemplate.send(ordersTopic, orderId, evt);

        // Store order so it can be retrieved via GET
        orderStore.put(orderId, evt);

        return new CreateOrderResponse(orderId, "PUBLISHED");
    }

    @GetMapping
    public Collection<OrderCreated> list() {
        return orderStore.values();
    }

    @GetMapping("/{orderId}")
    public OrderCreated getById(@PathVariable String orderId) {
        return orderStore.get(orderId);
    }

    @GetMapping("/{orderId}/status")
    public OrderStatusResponse status(@PathVariable String orderId) {
        var s = store.get(orderId);
        return new OrderStatusResponse(orderId, s == null ? "UNKNOWN" : s.name());
    }

    public record CreateOrderRequest(
            @NotBlank String sku,
            @Min(1) int quantity
    ) {}

    public record CreateOrderResponse(String orderId, String status) {}
    public record OrderStatusResponse(String orderId, String status) {}
}