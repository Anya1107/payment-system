package com.userservice.response;

import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String firstName,
        String lastName,
        boolean filled,
        AddressDto address,
        IndividualDto individual
) {}
