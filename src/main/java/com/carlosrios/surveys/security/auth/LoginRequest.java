package com.carlosrios.surveys.security.auth;

public record LoginRequest(
        String username,
        String password
) {
}
