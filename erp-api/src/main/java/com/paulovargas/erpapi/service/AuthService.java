package com.paulovargas.erpapi.service;

import com.paulovargas.erpapi.dto.AuthRequest;
import com.paulovargas.erpapi.dto.AuthResponse;
import com.paulovargas.erpapi.entity.Client;
import com.paulovargas.erpapi.repository.ClientRepository;
import com.paulovargas.erpapi.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final ClientRepository clientRepository;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            ClientRepository clientRepository,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.clientRepository = clientRepository;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        String token = jwtService.generateToken(client);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setExpiresIn(jwtService.getExpirationMillis());
        response.setClientId(client.getId());
        response.setName(client.getName());
        response.setEmail(client.getEmail());
        response.setUserType(client.getUserType());
        return response;
    }
}

