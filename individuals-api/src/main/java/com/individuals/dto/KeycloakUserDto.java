package com.individuals.dto;

public record KeycloakUserDto(
        String id,
        String email,
        Long createdTimestamp
) {}
