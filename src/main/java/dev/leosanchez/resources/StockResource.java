package dev.leosanchez.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import dev.leosanchez.dto.StockResponse;
import dev.leosanchez.interceptors.Cached;
import dev.leosanchez.interceptors.CachedInvalidate;
import dev.leosanchez.interceptors.CachedInvalidateAll;
import dev.leosanchez.interceptors.CachedKey;
import dev.leosanchez.services.StockService;

@Path("/product")
public class StockResource {

    @Inject
    StockService stockService;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Cached(cacheName = "cache-stock-request")
    @Path("/{name}")
    public StockResponse check (@PathParam("name") String productName) throws Exception {
        return stockService.getStock(productName);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @CachedInvalidate(cacheName = "cache-stock-request")
    @Path("/purchase")
    public String purchase (@CachedKey @QueryParam ("product") String productName, @QueryParam ("quantity") Integer quantity) throws Exception {
        stockService.purchase(productName, quantity);
        return "Ok";
    }

    @GET
    @Path("/invalidate-all")
    @Produces(MediaType.APPLICATION_JSON)
    @CachedInvalidateAll(cacheName = "cache-stock-request")
    public String invalidateAll () {
        return "ok";
    }
}