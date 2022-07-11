package dev.leosanchez.dto;

import java.util.Date;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class StockResponse {
    private String product;
    private Integer availableStock;
    private Date lastUpdate;
    public StockResponse(String product, Integer availableStock) {
        this.product = product;
        this.availableStock = availableStock;
        this.lastUpdate = new Date();
    }
    public String getProduct() {
        return product;
    }
    public Integer getAvailableStock() {
        return availableStock;
    }
    public Date getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(){
        this.lastUpdate = new Date();
    }
}
