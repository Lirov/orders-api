package com.example.order_api.events;

import lombok.*;
import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor
public class InventoryReserved {
    private String orderId;
    private String sku;
    private int quantity;
    private Instant reservedAt;
}
