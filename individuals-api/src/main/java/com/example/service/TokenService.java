package com.example.service;

import com.example.client.KeycloakClient;
import com.example.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final KeycloakClient keycloakClient;

    public Mono<TokenResponse> getAccessToken(String username, String password) {
        return keycloakClient.requestToken(username, password);
    }

    public Mono<TokenResponse> getRefreshTokenFromAccess(String refreshToken) {
        return keycloakClient.refreshToken(refreshToken);
    }
}
