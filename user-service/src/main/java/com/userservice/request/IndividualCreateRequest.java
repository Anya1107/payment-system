package com.userservice.request;

public record IndividualCreateRequest(
        String passportNumber,
        String phoneNumber,
        String status
) {}
