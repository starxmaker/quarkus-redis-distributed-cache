package dev.leosanchez.models;

import java.util.Date;
import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class AuthenticationResponse {
    private String username;
    private String token;
    private Date dateIssued;
    private AuthenticationResponse(){}

    public static AuthenticationResponse generate(String username) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.username = username;
        response.token = UUID.randomUUID().toString();
        response.dateIssued = new Date();
        return response;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Date getDateIssued() {
        return dateIssued;
    }
    public void setDateIssued(Date dateIssued) {
        this.dateIssued = dateIssued;
    }
}
