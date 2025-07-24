package com.userservice.service;

import com.userservice.request.UserRegistrationRequest;
import com.userservice.request.UserUpdateRequest;
import com.userservice.response.UserDto;

import java.util.UUID;

public interface UserService {

    void register(UserRegistrationRequest userRegistrationRequest);

    UserDto getById(UUID id);

    UserDto getByEmail(String email);

    void delete(UUID id);

    void update(UUID id, UserUpdateRequest userUpdateRequest);
}
