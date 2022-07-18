package dev.leosanchez.repositories;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StockRepository {

    Map<String, Integer> stocks = new HashMap<> (){{
        put("APPLE", 50);
        put("BANANA", 40);
        put("GRAPES", 30);
    }};

    public Integer getStock(String product) throws Exception {
        Thread.sleep(3000); // lets simulate a request time
        Integer stock = stocks.get(product);
        if (stock == null) {
            throw new Exception("Product not found");
        }
        return stock;
    }

    public void reduceStock(String product, Integer quantity) throws Exception {
        Integer stock = stocks.get(product);
        if (stock == null) {
            throw new Exception("Product not found");
        }
        Integer newStock = stock - quantity;
        if (newStock < 0) {
            throw new Exception("Not enough stock");
        }
        stocks.put(product, newStock);
    }
    
    public Map<String,Integer> getAllStock() throws Exception {
        Thread.sleep(3000);
        return stocks;
    }
}
