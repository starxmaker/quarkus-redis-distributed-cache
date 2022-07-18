package dev.leosanchez.services;
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

}
