package dev.leosanchez.utils;

import java.util.Date;

import javax.ws.rs.core.Response;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets = {Response.class, Date.class})
public class MyReflectionConfiguration {
    
}
