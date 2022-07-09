package dev.leosanchez.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dev.leosanchez.interceptors.Cached;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Cached(cacheName = "cache-hello")
    public Response hello() {
        return Response.ok("Hello RESTEasy").build();
    }
}