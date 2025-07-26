package com.userservice.service;

import com.userservice.dto.UserDto;
import com.userservice.dto.UserRegistrationRequest;
import com.userservice.dto.UserUpdateRequest;

import java.util.UUID;

public interface UserService {

    void register(UserRegistrationRequest userRegistrationRequest);

    UserDto getById(UUID id);

    UserDto getByEmail(String email);

    void delete(UUID id);

    void update(UUID id, UserUpdateRequest userUpdateRequest);
}
