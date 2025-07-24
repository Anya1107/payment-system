package com.userservice.response;

import java.util.UUID;

public record AddressDto(
        UUID id,
        String address,
        String zipCode,
        String city,
        String state
) {}
