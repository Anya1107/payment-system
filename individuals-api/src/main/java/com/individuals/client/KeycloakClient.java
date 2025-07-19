package com.individuals.client;

import com.individuals.dto.*;
import com.individuals.exception.CustomAuthException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.individuals.util.Constants.*;

@Component
public class KeycloakClient {
    private final WebClient webClient;
    private final String realmName;
    private final String clientId;
    private final String clientSecret;
    private final ReactiveJwtDecoder jwtDecoder;

    public KeycloakClient(
            @Value("${keycloak.url}") String keycloakUrl,
            @Value("${keycloak.realm}") String realmName,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret,
            ReactiveJwtDecoder jwtDecoder
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(keycloakUrl)
                .build();
        this.realmName = realmName;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.jwtDecoder = jwtDecoder;
    }

    public Mono<TokenResponse> requestToken(String username, String password) {
        return webClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realmName)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(GRANT_TYPE, PASSWORD_PARAM)
                        .with(CLIENT_ID_PARAM, clientId)
                        .with(CLIENT_SECRET_PARAM, clientSecret)
                        .with(USERNAME_PARAM, username)
                        .with(PASSWORD_PARAM, password)
                        .with(SCOPE_PARAM, OPEN_ID_PARAM))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> handleErrorResponse(response, INVALID_CREDENTIALS_ERROR_MESSAGE, HttpStatus.UNAUTHORIZED))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> handleErrorResponse(response, KEYCLOAK_INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR))
                .bodyToMono(String.class)
                .map(json -> new Gson().fromJson(json, TokenResponse.class));
    }

    public Mono<Void> createUser(String username, String firstName, String lastName, String email, String password) {
        return requestServiceToken()
                .flatMap(tokenResponse ->
                        webClient.post()
                                .uri("/admin/realms/{realm}/users", realmName)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + tokenResponse.getAccessToken())
                                .bodyValue(getCreateUserBody(username, firstName, lastName, email, password))
                                .retrieve()
                                .onStatus(status -> status == HttpStatus.BAD_REQUEST,
                                        response -> handleErrorResponse(response, VALIDATION_ERROR_MESSAGE, HttpStatus.BAD_REQUEST))
                                .onStatus(status -> status == HttpStatus.CONFLICT,
                                        response -> handleErrorResponse(response,USER_ALREADY_EXISTS_ERROR_MESSAGE, HttpStatus.CONFLICT))
                                .onStatus(HttpStatusCode::is5xxServerError,
                                        response -> handleErrorResponse(response, KEYCLOAK_INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR))
                                .toBodilessEntity()
                                .then()
                );
    }

    public Mono<TokenResponse> requestServiceToken() {
        return webClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realmName)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(GRANT_TYPE, CLIENT_CREDENTIALS_PARAM)
                        .with(CLIENT_ID_PARAM, clientId)
                        .with(CLIENT_SECRET_PARAM, clientSecret))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> handleErrorResponse(response, INVALID_CLIENT_CREDENTIALS_ERROR_MESSAGE, HttpStatus.UNAUTHORIZED))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> handleErrorResponse(response, KEYCLOAK_INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR))
                .bodyToMono(String.class)
                .map(json -> new Gson().fromJson(json, TokenResponse.class));
    }

    public Mono<TokenResponse> refreshToken(String refreshToken) {
        return webClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realmName)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(GRANT_TYPE, REFRESH_TOKEN_PARAM)
                        .with(CLIENT_ID_PARAM, clientId)
                        .with(CLIENT_SECRET_PARAM, clientSecret)
                        .with(REFRESH_TOKEN_PARAM, refreshToken))
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST,
                        response -> handleErrorResponse(response, INVALID_REFRESH_TOKEN_ERROR_MESSAGE, HttpStatus.UNAUTHORIZED))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> handleErrorResponse(response, KEYCLOAK_INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR))
                .bodyToMono(String.class)
                .map(json -> new Gson().fromJson(json, TokenResponse.class));
    }

    public Mono<UserInfoResponse> fetchUserInfo(String accessToken) {
        String token = getTokenWithoutPrefix(accessToken);

        return decodeJwt(token)
                .flatMap(this::extractUserId)
                .flatMap(userId ->
                        requestServiceToken()
                                .flatMap(adminToken ->
                                        Mono.zip(
                                                fetchUserProfile(adminToken.getAccessToken(), userId),
                                                fetchUserRoles(adminToken.getAccessToken(), userId)
                                        ).map(tuple -> mapToUserInfoResponse(tuple.getT1(), tuple.getT2()))
                                )
                );
    }

    private String getTokenWithoutPrefix(String token) {
        return token.startsWith(BEARER_PREFIX) ? token.substring(TOKEN_BEGIN_INDEX) : token;
    }

    private Mono<Jwt> decodeJwt(String token) {
        return jwtDecoder.decode(token)
                .onErrorMap(e -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_ACCESS_TOKEN_ERROR_MESSAGE, e));
    }

    private Mono<String> extractUserId(Jwt jwt) {
        String userId = jwt.getSubject();

        if (userId == null || userId.isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, TOKEN_WITHOUT_SUBJECT_ERROR_MESSAGE));
        }

        return Mono.just(userId);
    }

    private Mono<KeycloakUserDto> fetchUserProfile(String adminToken, String userId) {
        return webClient.get()
                .uri("/admin/realms/{realm}/users/{id}", realmName, userId)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + adminToken)
                .retrieve()
                .onStatus(status -> status == HttpStatus.UNAUTHORIZED,
                        response -> handleErrorResponse(response, INVALID_ACCESS_TOKEN_ERROR_MESSAGE, HttpStatus.UNAUTHORIZED))
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> handleErrorResponse(response, USER_NOT_FOUND_ERROR_MESSAGE, HttpStatus.NOT_FOUND))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> handleErrorResponse(response, KEYCLOAK_INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR))
                .bodyToMono(KeycloakUserDto.class);
    }

    private Mono<List<String>> fetchUserRoles(String adminToken, String userId) {
        return webClient.get()
                .uri("/admin/realms/{realm}/users/{id}/role-mappings/realm", realmName, userId)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + adminToken)
                .retrieve()
                .onStatus(status -> status == HttpStatus.UNAUTHORIZED,
                        response -> handleErrorResponse(response, INVALID_ACCESS_TOKEN_ERROR_MESSAGE, HttpStatus.UNAUTHORIZED))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> handleErrorResponse(response, KEYCLOAK_INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR))
                .bodyToFlux(KeycloakRoleDto.class)
                .map(KeycloakRoleDto::name)
                .collectList();
    }

    private UserInfoResponse mapToUserInfoResponse(KeycloakUserDto user, List<String> roles) {
        OffsetDateTime createdAt = OffsetDateTime.ofInstant(Instant.ofEpochMilli(user.createdTimestamp()), ZoneOffset.UTC);

        return new UserInfoResponse()
                .id(user.id())
                .email(user.email())
                .roles(roles)
                .createdAt(createdAt);
    }

    private Mono<CustomAuthException> handleErrorResponse(ClientResponse response, String defaultMessage, HttpStatus defaultStatus) {
        return response.bodyToMono(ErrorResponse.class)
                .map(body -> {
                    int status = Objects.requireNonNullElse(body.getStatus(), defaultStatus.value());
                    String error = body.getError() != null ? body.getError() : defaultMessage;
                    return new CustomAuthException(error, status);
                });
    }

    private Map<String, Object> getCreateUserBody(String username, String firstName, String lastName, String email, String password) {
        return Map.of(
                USERNAME_PARAM, username,
                EMAIL_PARAM, email,
                FIRST_NAME_PARAM, firstName,
                LAST_NAME_PARAM, lastName,
                ENABLED_PARAM, true,
                EMAIL_VERIFIED_PARAM, true,
                REQUIRED_ACTIONS_PARAM, List.of(),
                CREDENTIALS_PARAM, List.of(
                        Map.of(
                                TYPE_PARAM, PASSWORD_PARAM,
                                VALUE_PARAM, password,
                                TEMPORARY_PARAM, false
                        )
                )
        );
    }
}
