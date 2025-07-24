package com.userservice.response;

import java.util.UUID;

public record IndividualDto(
        UUID id,
        String passportNumber,
        String phoneNumber,
        String status
) {}
