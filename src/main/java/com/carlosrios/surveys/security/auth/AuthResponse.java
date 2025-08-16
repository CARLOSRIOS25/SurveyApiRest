package com.carlosrios.surveys.security.auth;

import lombok.Builder;

@Builder
public record AuthResponse(
        String token
) {
}
