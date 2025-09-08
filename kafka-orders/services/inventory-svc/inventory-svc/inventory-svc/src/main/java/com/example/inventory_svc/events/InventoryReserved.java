package com.example.inventory_svc.events;

import lombok.*;
import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor
public class InventoryReserved {
    private String orderId;
    private String sku;
    private int quantity;
    private Instant reservedAt;
}
