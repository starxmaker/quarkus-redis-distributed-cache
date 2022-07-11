package dev.leosanchez.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class AuthenticationRequest {
    private String username;
    private String password;
    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public String password() {
        return password;
    }
}
