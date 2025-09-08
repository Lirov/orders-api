package com.example.inventory_svc.core;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StockService {
    private final Map<String, Integer> stock = new ConcurrentHashMap<>();

    public StockService() {
        // seed a few SKUs for demo
        stock.put("SKU-ABC", 10);
        stock.put("SKU-XYZ", 3);
        stock.put("SKU-PREMIUM", 0);
    }

    public synchronized boolean tryReserve(String sku, int qty) {
        int available = stock.getOrDefault(sku, 0);
        if (available >= qty) {
            stock.put(sku, available - qty);
            return true;
        }
        return false;
    }

    public Map<String, Integer> snapshot() {
        return Map.copyOf(stock);
    }
}