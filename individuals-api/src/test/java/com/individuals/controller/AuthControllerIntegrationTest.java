package com.individuals.controller;

import com.individuals.dto.*;
import com.individuals.service.UserService;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


@SpringBootTest
@AutoConfigureWebTestClient
public class AuthControllerIntegrationTest {
    private final String EMAIL = "testemail@example.com";
    private final String PASSWORD = "test";
    private final String USERNAME = "testuser";
    private final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserService userService;

    @Autowired
    private ReactiveJwtDecoder jwtDecoder;

    static KeycloakContainer keycloakContainer;

    @BeforeAll
    static void startKeycloak() {
        keycloakContainer = new KeycloakContainer()
                .withRealmImportFile("realm-config.json");

        keycloakContainer.start();

        System.setProperty("KEYCLOAK_URL", keycloakContainer.getAuthServerUrl());
        System.setProperty("KEYCLOAK_ISSUER_URI", keycloakContainer.getAuthServerUrl() + "/realms/myrealm");
        System.setProperty("KEYCLOAK_JWK_SET_URI", keycloakContainer.getAuthServerUrl() + "/realms/myrealm/protocol/openid-connect/certs");
    }

    @AfterAll
    static void stopKeycloak() {
        if (keycloakContainer != null) {
            keycloakContainer.stop();
        }
    }

    @Test
    void success_registration() {
        UserRegistrationRequest request = new UserRegistrationRequest()
                .username("firstTestuser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("firsttest@example.com")
                .password("1234")
                .confirmPassword("1234");

        webTestClient.post()
                .uri("/v1/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TokenResponse.class)
                .value(tokenResponse -> {
                    Assertions.assertNotNull(tokenResponse.getAccessToken());
                    Assertions.assertNotNull(tokenResponse.getRefreshToken());
                });
    }

    @Test
    void registrationFail_existingUser_returnConflict() {
        UserRegistrationRequest request = new UserRegistrationRequest()
                .username(USERNAME)
                .firstName("testFirstName")
                .lastName("testLastName")
                .email(EMAIL)
                .password("1234")
                .confirmPassword("1234");

        webTestClient.post()
                .uri("/v1/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void registrationFail_userWithInvalidEmail_returnBadRequest() {
        UserRegistrationRequest request = new UserRegistrationRequest()
                .username(USERNAME)
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("email")
                .password("1234")
                .confirmPassword("1234");

        webTestClient.post()
                .uri("/v1/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void success_login() {
        UserLoginRequest request = new UserLoginRequest()
                .email(EMAIL)
                .password(PASSWORD);

        webTestClient.post()
                .uri("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserLoginRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponse.class)
                .value(tokenResponse -> {
                    Assertions.assertNotNull(tokenResponse.getAccessToken());
                    Assertions.assertNotNull(tokenResponse.getRefreshToken());
                });
    }

    @Test
    void loginFail_wrongPassword_returnsUnauthorized() {
        var loginRequest = new UserLoginRequest()
                .email(EMAIL)
                .password("wrong password");

        webTestClient.post()
                .uri("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginRequest), UserLoginRequest.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void success_refreshToken_returnsNewTokens() {
        UserLoginRequest loginRequest = new UserLoginRequest()
                .email(EMAIL)
                .password(PASSWORD);

        TokenResponse initialToken = webTestClient.post()
                .uri("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginRequest), UserLoginRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponse.class)
                .returnResult()
                .getResponseBody();

        assert initialToken != null;

        String refreshToken = initialToken.getRefreshToken();

        TokenRefreshRequest refreshRequest = new TokenRefreshRequest()
                .refreshToken(refreshToken);

        webTestClient.post()
                .uri("/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(refreshRequest), TokenRefreshRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponse.class)
                .value(tokenResponse -> {
                    Assertions.assertNotNull(tokenResponse.getAccessToken());
                    Assertions.assertNotNull(tokenResponse.getRefreshToken());
                    Assertions.assertNotEquals(tokenResponse.getAccessToken(), initialToken.getAccessToken());
                });
    }

    @Test
    void refreshTokenFail_invalidToken_returnsUnauthorized() {
        String refreshToken = "invalidRefreshToken";

        TokenRefreshRequest refreshRequest = new TokenRefreshRequest()
                .refreshToken(refreshToken);

        webTestClient.post()
                .uri("/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(refreshRequest), TokenRefreshRequest.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getCurrentUser_missingToken_returnsUnauthorized() {
        webTestClient.get()
                .uri("/v1/auth/me")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
