package com.userservice.request;

public record UserCreateRequest(
        String email,
        String firstName,
        String lastName,
        String secretKey
) {}