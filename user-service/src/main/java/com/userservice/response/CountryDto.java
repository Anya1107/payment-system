package com.userservice.response;

public record CountryDto(
        Integer id,
        String name,
        String alpha2,
        String alpha3,
        String status
) {}
