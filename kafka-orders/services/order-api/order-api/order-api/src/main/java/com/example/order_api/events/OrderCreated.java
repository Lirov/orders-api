package com.example.order_api.events;

import lombok.*;
import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor
public class OrderCreated {
    private String orderId;
    private String sku;
    private int quantity;
    private Instant createdAt;
}
