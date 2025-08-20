package com.carlosrios.surveys.security.auth;

import com.carlosrios.surveys.infra.exceptions.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "RestController for authentication with JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "login")
    @Operation(
            summary = "Login", description = "Authenticate a user to returns a token for authorization", tags = {"Authentication"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Authentication request by username and password", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful Authentication",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        //returns AuthResponse Object
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping(value = "register")
    @Operation(
            summary = "Register", description = "Register a user and immediately returns a token for authorization", tags = {"Authentication"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Register request by username and password", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterRequest.class))),
            responses = @ApiResponse(responseCode = "200", description = "Successful Registration",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    )
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        //returns AuthResponse Object
        return ResponseEntity.ok(authService.register(registerRequest));
    }

}
