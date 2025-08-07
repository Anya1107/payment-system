package com.userservice.util;

import com.userservice.dto.AddressCreateRequest;
import com.userservice.dto.IndividualCreateRequest;
import com.userservice.dto.UserCreateRequest;
import com.userservice.dto.UserRegistrationRequest;

public class TestDataCreator {

    public static UserRegistrationRequest buildRegistrationRequest(String email) {
        return new UserRegistrationRequest()
                .user(
                        new UserCreateRequest()
                                .email(email)
                                .firstName("firstName")
                                .lastName("lastName")
                                .secretKey("secretKey")
                )
                .address(
                        new AddressCreateRequest()
                                .address("address")
                                .city("city")
                                .countryId(1)
                                .state("state")
                                .zipCode("zipCode")
                )
                .individual(
                        new IndividualCreateRequest()
                                .passportNumber("12345678")
                                .phoneNumber("32983298")
                                .status("active")
                );
    }
}
