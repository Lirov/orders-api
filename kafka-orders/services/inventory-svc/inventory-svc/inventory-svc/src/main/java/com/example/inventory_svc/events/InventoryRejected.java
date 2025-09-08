package com.example.inventory_svc.events;

import lombok.*;
import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor
public class InventoryRejected {
    private String orderId;
    private String sku;
    private int quantityRequested;
    private String reason;
    private Instant rejectedAt;
}
