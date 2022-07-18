package dev.leosanchez.services;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import dev.leosanchez.dto.StockResponse;
import dev.leosanchez.repositories.StockRepository;

@ApplicationScoped
public class StockService {

    @Inject
    StockRepository stockRepository;
    
    public StockResponse getStock(String product) throws Exception {
        Integer stock = stockRepository.getStock(product);
        return new StockResponse(product, stock);
    }

    public void purchase(String productName, Integer quantity) throws Exception {
        stockRepository.reduceStock(productName, quantity);   
    }

    public List<StockResponse> getAllStock() throws Exception  {
        Map<String, Integer> stocks = stockRepository.getAllStock();
        List<StockResponse> stockResponses = new ArrayList<>();
        for (String product : stocks.keySet()) {
            stockResponses.add(new StockResponse(product, stocks.get(product)));
        }
        return stockResponses;
    }

}
