package com.userservice.request;

public record UserRegistrationRequest(
    UserCreateRequest user,
    AddressCreateRequest address,
    IndividualCreateRequest individual
){}
