package com.carlosrios.surveys.security.auth;


import com.carlosrios.surveys.entities.Role;
import com.carlosrios.surveys.entities.User;
import com.carlosrios.surveys.repositories.UserRepository;
import com.carlosrios.surveys.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager; //this has to be configured by a bean in ApplicationConfig
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    //request the auth through authenticationManager for the login
    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.getToken(user);
        return AuthResponse.builder().token(token).build();
    }


    //register a new user creating the token and returning to the client
    public AuthResponse register(RegisterRequest registerRequest) {
        User user = User.builder()
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return AuthResponse
                .builder()
                .token(jwtService.getToken(user))
                .build();
    }
}
