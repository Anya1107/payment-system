package com.userservice.mapper;

import com.userservice.entity.Address;
import com.userservice.entity.Individual;
import com.userservice.entity.User;
import com.userservice.response.AddressDto;
import com.userservice.response.IndividualDto;
import com.userservice.response.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    AddressDto toDto(Address address);

    IndividualDto toDto(Individual individual);
}
