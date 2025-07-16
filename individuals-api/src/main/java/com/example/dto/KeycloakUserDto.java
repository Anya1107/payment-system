package com.example.dto;

public record KeycloakUserDto(
        String id,
        String email,
        Long createdTimestamp
) {}
