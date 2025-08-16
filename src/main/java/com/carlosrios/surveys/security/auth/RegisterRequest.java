package com.carlosrios.surveys.security.auth;


public record RegisterRequest(
        String username,
        String password
) {
}
