package com.example.order_api.core;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderStore {
    public enum Status { NEW, RESERVED, REJECTED }

    // orderId -> status
    private final Map<String, Status> map = new ConcurrentHashMap<>();

    public void put(String orderId, Status status) { map.put(orderId, status); }
    public Status get(String orderId) { return map.get(orderId); }
}
