package com.feign.client;

import com.userservice.dto.UserDto;
import com.userservice.dto.UserRegistrationRequest;
import com.userservice.dto.UserUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "userServiceClient", url = "${user.service.url}")
public interface UserServiceClient {

    @PostMapping("/api/v1/user")
    UUID createUser(@RequestBody UserRegistrationRequest userRegistrationRequest);

    @GetMapping("/api/v1/user/{id}")
    UserDto getUserById(@PathVariable("id") UUID id);

    @GetMapping("/api/v1/user")
    UserDto getUserByEmail(@RequestParam("email") String email);

    @PutMapping("/api/v1/user/{id}")
    void updateUser(@PathVariable("id") UUID id, @RequestBody UserUpdateRequest userUpdateRequest);

    @DeleteMapping("/api/v1/user/{id}")
    void deleteUser(@PathVariable("id") UUID id);
}
