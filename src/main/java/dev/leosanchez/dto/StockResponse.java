package dev.leosanchez.dto;

import java.util.Date;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class StockResponse {
    private String product;
    private Integer availableStock;
    private Date lastUpdate;
    public StockResponse() {
    }
    public StockResponse(String product, Integer availableStock) {
        this.product = product;
        this.availableStock = availableStock;
        this.lastUpdate = new Date();
    }

    public String getProduct() {
        return product;
    }
    public void setProduct(String product) {
        this.product = product;
    }
    public Integer getAvailableStock() {
        return availableStock;
    }
    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }
    public Date getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
