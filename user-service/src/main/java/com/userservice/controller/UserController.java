package com.userservice.controller;

import com.userservice.entity.User;
import com.userservice.request.UserRegistrationRequest;
import com.userservice.request.UserUpdateRequest;
import com.userservice.service.UserService;
import com.userservice.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        userService.register(userRegistrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Object> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getByEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable UUID id, @RequestBody UserUpdateRequest userUpdateRequest) {
        userService.update(id, userUpdateRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
