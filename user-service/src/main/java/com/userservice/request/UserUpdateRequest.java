package com.userservice.request;

public record UserUpdateRequest(
        String email,
        String firstName,
        String lastName,
        Boolean filled,
        IndividualUpdateRequest individual,
        AddressUpdateRequest address
) {
}
