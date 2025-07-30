package com.individuals.service;

import com.individuals.client.KeycloakClient;
import com.individuals.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static com.individuals.util.Constants.*;


@Service
@RequiredArgsConstructor
public class UserService {
    private final KeycloakClient keycloakClient;
    private final TokenService tokenService;
    private final ReactiveJwtDecoder jwtDecoder;

    private final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public Mono<TokenResponse> register(UserRegistrationRequest request) {
        return keycloakClient.createUser(request.getUser().getId(), request.getUser().getUsername(), request.getUser().getFirstName(), request.getUser().getLastName(), request.getUser().getEmail(), request.getUser().getPassword())
                .doOnSubscribe(s -> LOG.info(USER_CREATING_LOG, request.getUser().getEmail()))
                .doOnSuccess(v -> LOG.info(USER_CREATION_SUCCESS_LOG, request.getUser().getEmail()))
                .doOnError(e -> LOG.error(USER_CREATION_FAILED_LOG, request.getUser().getEmail(), e))
                .then(loginUser(request.getUser().getEmail(), request.getUser().getPassword()));
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

    public Mono<UserInfoResponse> getCurrentUser(String userId) {
        return keycloakClient.fetchUserInfo(userId)
                .doOnSubscribe(s -> LOG.info(USER_FETCHING_LOG))
                .doOnSuccess(user -> LOG.info(USER_FETCHING_SUCCESS_LOG, user.getEmail()))
                .doOnError(e -> LOG.error(USER_FETCHING_FAILED_LOG, e));
    }

    public Mono<Void> deleteUser(String userId) {
        return keycloakClient.deleteUser(userId)
                .doOnSubscribe(s -> LOG.info(USER_DELETION_LOG))
                .doOnSuccess(user -> LOG.info(USER_DELETION_SUCCESS_LOG, userId))
                .doOnError(e -> LOG.error(USER_DELETION_FAILED_LOG, e));
    }

    private Mono<TokenResponse> loginUser(String email, String password) {
        return tokenService.getAccessToken(email, password)
                .doOnSubscribe(s -> LOG.info(USER_LOGIN_LOG, email))
                .doOnSuccess(token -> LOG.info(USER_LOGIN_SUCCESS_LOG, email))
                .doOnError(e -> LOG.error(USER_LOGIN_FAILED_LOG, e));
    }

    public Mono<String> extractUserId(String accessToken) {
        String token = getTokenWithoutPrefix(accessToken);

        return decodeJwt(token)
                .flatMap(this::getUserId);
    }

    private String getTokenWithoutPrefix(String token) {
        return token.startsWith(BEARER_PREFIX) ? token.substring(TOKEN_BEGIN_INDEX) : token;
    }

    private Mono<Jwt> decodeJwt(String token) {
        return jwtDecoder.decode(token)
                .onErrorMap(e -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_ACCESS_TOKEN_ERROR_MESSAGE, e));
    }

    private Mono<String> getUserId(Jwt jwt) {
        String userId = jwt.getSubject();

        if (userId == null || userId.isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, TOKEN_WITHOUT_SUBJECT_ERROR_MESSAGE));
        }

        return Mono.just(userId);
    }
}
