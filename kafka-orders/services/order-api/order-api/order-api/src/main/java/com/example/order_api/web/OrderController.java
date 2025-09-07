package com.example.order_api.web;

import com.example.order_api.events.OrderCreated;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@Validated
@RequiredArgsConstructor
public class OrderController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.topics.ordersCreated}")
    private String ordersTopic;

    @PostMapping
    public CreateOrderResponse create(@RequestBody CreateOrderRequest req) {
        String orderId = UUID.randomUUID().toString();
        OrderCreated evt = new OrderCreated(orderId, req.sku(), req.quantity(), Instant.now());

        kafkaTemplate.send(ordersTopic, orderId, evt);

        return new CreateOrderResponse(orderId, "PUBLISHED");
    }

    public record CreateOrderRequest(
            @NotBlank String sku,
            @Min(1) int quantity
    ) {}

    public record CreateOrderResponse(String orderId, String status) {}
}
