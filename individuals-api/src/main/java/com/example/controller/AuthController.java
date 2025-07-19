package com.example.controller;

import com.example.dto.*;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/registration")
    public Mono<ResponseEntity<TokenResponse>> register(@RequestBody UserRegistrationRequest request) {
        return userService.register(request)
                .map(tokenResponse -> ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenResponse>> login(@RequestBody UserLoginRequest request) {
        return userService.login(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/refresh-token")
    public Mono<ResponseEntity<TokenResponse>> refreshToken(@RequestBody TokenRefreshRequest request) {
        return userService.refreshToken(request)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<UserInfoResponse>> getCurrentUser(@RequestHeader(AUTHORIZATION) String accessToken) {
        return userService.getCurrentUser(accessToken)
                .map(ResponseEntity::ok);
    }
}
