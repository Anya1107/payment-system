package com.userservice.mapper;

import com.userservice.dto.AddressDto;
import com.userservice.dto.IndividualDto;
import com.userservice.dto.UserDto;
import com.userservice.entity.Address;
import com.userservice.entity.Individual;
import com.userservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    AddressDto toDto(Address address);

    IndividualDto toDto(Individual individual);
}
