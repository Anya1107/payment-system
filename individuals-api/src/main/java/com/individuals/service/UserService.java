package com.individuals.service;

import com.individuals.client.KeycloakClient;
import com.individuals.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.individuals.util.Constants.*;


@Service
@RequiredArgsConstructor
public class UserService {
    private final KeycloakClient keycloakClient;
    private final TokenService tokenService;

    private final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public Mono<TokenResponse> register(UserRegistrationRequest request) {
        return keycloakClient.createUser(request.getUsername(), request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword())
                .doOnSubscribe(s -> LOG.info(USER_CREATING_LOG, request.getEmail()))
                .doOnSuccess(v -> LOG.info(USER_CREATION_SUCCESS_LOG, request.getEmail()))
                .doOnError(e -> LOG.error(USER_CREATION_FAILED_LOG, request.getEmail(), e))
                .then(loginUser(request.getEmail(), request.getPassword()));
    }

    public Mono<TokenResponse> login(UserLoginRequest request) {
        return loginUser(request.getEmail(), request.getPassword());
    }

    public Mono<TokenResponse> refreshToken(TokenRefreshRequest request) {
        return tokenService.getRefreshTokenFromAccess(request.getRefreshToken())
                .doOnSubscribe(s -> LOG.info(TOKEN_REFRESHING_LOG))
                .doOnSuccess(token -> LOG.info(TOKEN_REFRESH_SUCCESS_LOG))
                .doOnError(e -> LOG.error(TOKEN_REFRESH_FAILED_LOG, e));
    }

    public Mono<UserInfoResponse> getCurrentUser(String accessToken) {
        return keycloakClient.fetchUserInfo(accessToken)
                .doOnSubscribe(s -> LOG.info(USER_FETCHING_LOG))
                .doOnSuccess(user -> LOG.info(USER_FETCHING_SUCCESS_LOG, user.getEmail()))
                .doOnError(e -> LOG.error(USER_FETCHING_FAILED_LOG, e));
    }

    private Mono<TokenResponse> loginUser(String email, String password) {
        return tokenService.getAccessToken(email, password)
                .doOnSubscribe(s -> LOG.info(USER_LOGIN_LOG, email))
                .doOnSuccess(token -> LOG.info(USER_LOGIN_SUCCESS_LOG, email))
                .doOnError(e -> LOG.error(USER_LOGIN_FAILED_LOG, e));
    }
}
