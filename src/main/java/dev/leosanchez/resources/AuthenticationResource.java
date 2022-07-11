package dev.leosanchez.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import dev.leosanchez.interceptors.Cached;
import dev.leosanchez.interceptors.CachedInvalidate;
import dev.leosanchez.interceptors.CachedInvalidateAll;
import dev.leosanchez.models.AuthenticationRequest;
import dev.leosanchez.models.AuthenticationResponse;

@Path("/authenticate")
public class AuthenticationResource {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Cached(cacheName = "cache-user-authentication")
    public AuthenticationResponse login (AuthenticationRequest request) throws Exception {
        Thread.sleep(2000);
        return AuthenticationResponse.generate(request.getUsername());
    }

    @POST
    @Path("/invalidate")
    @Produces(MediaType.APPLICATION_JSON)
    @CachedInvalidate(cacheName = "cache-user-authentication")
    public String logout (AuthenticationRequest request) {
        return "ok";
    }

    @GET
    @Path("/invalidate-all")
    @Produces(MediaType.APPLICATION_JSON)
    @CachedInvalidateAll(cacheName = "cache-user-authentication")
    public String logoutAll () {
        return "ok";
    }
}