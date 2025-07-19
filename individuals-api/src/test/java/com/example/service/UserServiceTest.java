package com.example.service;

import com.example.client.KeycloakClient;
import com.example.dto.*;
import com.example.exception.CustomAuthException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private KeycloakClient keycloakClient;

    private TokenService tokenService;

    private UserService userService;

    private final String USERNAME = "testuser";
    private final String EMAIL = "testuser@example.com";
    private final String PASSWORD = "testpassword";
    private final String ACCESS_TOKEN = "accessToken";
    private final String REFRESH_TOKEN = "refreshToken";
    private final String FIRST_NAME = "firstName";
    private final String LAST_NAME = "lastName";

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(keycloakClient);

        userService = new UserService(keycloakClient, tokenService);
    }

    @Test
    void login_success() {
        TokenResponse expectedTokenResponse = new TokenResponse();

        expectedTokenResponse.setAccessToken(ACCESS_TOKEN);
        expectedTokenResponse.setRefreshToken(REFRESH_TOKEN);

        when(keycloakClient.requestToken(EMAIL, PASSWORD)).thenReturn(Mono.just(expectedTokenResponse));

        StepVerifier.create(userService.login(new UserLoginRequest().email(EMAIL).password(PASSWORD)))
                .expectNextMatches(token -> token.getAccessToken().equals(ACCESS_TOKEN))
                .verifyComplete();

        verify(keycloakClient).requestToken(EMAIL, PASSWORD);
    }

    @Test
    void login_failure_invalidCredentials() {
        String password = "wrongpassword";

        when(keycloakClient.requestToken(EMAIL, password)).thenReturn(Mono.error(new CustomAuthException("Unauthorized", 401)));

        StepVerifier.create(userService.login(new UserLoginRequest().email(EMAIL).password(password)))
                .expectErrorSatisfies(ex -> {
                    Assertions.assertInstanceOf(CustomAuthException.class, ex);
                    Assertions.assertEquals(401, ((CustomAuthException) ex).getStatus());
                })
                .verify();

        verify(keycloakClient).requestToken(EMAIL, password);
    }

    @Test
    void register_success() {
        UserRegistrationRequest request = new UserRegistrationRequest()
                .username(USERNAME)
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .password(PASSWORD);

        TokenResponse expectedTokenResponse = new TokenResponse()
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN);

        when(keycloakClient.createUser(USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD)).thenReturn(Mono.empty());
        when(keycloakClient.requestToken(EMAIL, PASSWORD)).thenReturn(Mono.just(expectedTokenResponse));

        StepVerifier.create(userService.register(request))
                .expectNextMatches(token -> token.getAccessToken().equals(ACCESS_TOKEN))
                .verifyComplete();

        verify(keycloakClient).createUser(USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);
        verify(keycloakClient).requestToken(EMAIL, PASSWORD);
    }

    @Test
    void register_failure_userExists() {
        UserRegistrationRequest request = new UserRegistrationRequest()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .password(PASSWORD);

        when(keycloakClient.createUser(USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD)).thenReturn(Mono.error(new CustomAuthException("User already exists", 409)));
        when(tokenService.getAccessToken(any(), any())).thenReturn(Mono.never());

        StepVerifier.create(userService.register(request))
                .expectErrorSatisfies(ex -> {
                    Assertions.assertInstanceOf(CustomAuthException.class, ex);
                    Assertions.assertEquals(409, ((CustomAuthException) ex).getStatus());
                })
                .verify();

        verify(keycloakClient).createUser(USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);
        verifyNoMoreInteractions(keycloakClient);
    }

    @Test
    void refreshToken_success() {
        TokenRefreshRequest request = new TokenRefreshRequest()
                .refreshToken(REFRESH_TOKEN);

        TokenResponse expectedToken = new TokenResponse()
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN);

        when(keycloakClient.refreshToken(REFRESH_TOKEN)).thenReturn(Mono.just(expectedToken));

        StepVerifier.create(userService.refreshToken(request))
                .expectNextMatches(token -> token.getAccessToken().equals(ACCESS_TOKEN))
                .verifyComplete();

        verify(keycloakClient).refreshToken(REFRESH_TOKEN);
    }

    @Test
    void refreshToken_failure_invalidToken() {
        String invalidRefreshToken = "invalidRefreshToken";

        TokenRefreshRequest request = new TokenRefreshRequest()
                .refreshToken(invalidRefreshToken);

        when(keycloakClient.refreshToken(invalidRefreshToken)).thenReturn(Mono.error(new CustomAuthException("Invalid refresh token", 401)));

        StepVerifier.create(userService.refreshToken(request))
                .expectErrorSatisfies(ex -> {
                    Assertions.assertInstanceOf(CustomAuthException.class, ex);
                    Assertions.assertEquals(401, ((CustomAuthException) ex).getStatus());
                })
                .verify();

        verify(keycloakClient).refreshToken(invalidRefreshToken);
    }

    @Test
    void getCurrentUser_success() {
        UserInfoResponse userInfo = new UserInfoResponse()
                .id("user-id")
                .email(USERNAME)
                .roles(List.of("user"));

        when(keycloakClient.fetchUserInfo(ACCESS_TOKEN)).thenReturn(Mono.just(userInfo));

        StepVerifier.create(userService.getCurrentUser(ACCESS_TOKEN))
                .expectNextMatches(info -> info.getEmail().equals(USERNAME) && info.getRoles().contains("user"))
                .verifyComplete();

        verify(keycloakClient).fetchUserInfo(ACCESS_TOKEN);
    }

    @Test
    void getCurrentUser_failure_invalidToken() {
        String invalidAccessToken = "invalidAccessToken";

        when(keycloakClient.fetchUserInfo(invalidAccessToken)).thenReturn(Mono.error(new CustomAuthException("Invalid token", 401)));

        StepVerifier.create(userService.getCurrentUser(invalidAccessToken))
                .expectErrorSatisfies(ex -> {
                    Assertions.assertInstanceOf(CustomAuthException.class, ex);
                    Assertions.assertEquals(401, ((CustomAuthException) ex).getStatus());
                })
                .verify();

        verify(keycloakClient).fetchUserInfo(invalidAccessToken);
    }
}