package com.userservice.request;

public record IndividualUpdateRequest(
        String passportNumber,
        String phoneNumber,
        String status
) {}