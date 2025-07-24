package com.userservice.request;

public record AddressCreateRequest(
        String address,
        String zipCode,
        String city,
        String state,
        Integer countryId
) { }