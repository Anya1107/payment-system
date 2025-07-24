package com.userservice.request;

public record AddressUpdateRequest(
        String address,
        String city,
        String state,
        String zipCode,
        Integer countryId
) {
}
