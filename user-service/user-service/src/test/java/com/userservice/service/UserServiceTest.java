package com.userservice.service;

import com.userservice.dto.UserDto;
import com.userservice.dto.UserRegistrationRequest;
import com.userservice.dto.UserUpdateRequest;
import com.userservice.entity.Address;
import com.userservice.entity.User;
import com.userservice.exception.UserAlreadyExistsException;
import com.userservice.exception.UserNotFoundException;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.userservice.controller.UserControllerIntegrationTest.buildValidRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private IndividualService individualService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private final UUID userId = UUID.randomUUID();
    private final String email = "test@example.com";

    @Test
    void success_registration() {
        Address address = new Address();
        User user = new User();

        UserRegistrationRequest request = buildValidRequest("user@example.com");

        when(userRepository.existsByEmail(request.getUser().getEmail())).thenReturn(false);
        when(addressService.create(request.getAddress())).thenReturn(address);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.register(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registrationFail_existingUser_returnConflict() {
        UserRegistrationRequest request = buildValidRequest("user@example.com");

        when(userRepository.existsByEmail(request.getUser().getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void success_getById() {
        User user = new User();
        UserDto userDto = new UserDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getById(userId);

        assertEquals(userDto, result);
    }

    @Test
    void getById_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    void success_getByEmail() {
        User user = new User();
        UserDto userDto = new UserDto();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getByEmail(email);

        assertEquals(userDto, result);
    }

    @Test
    void getByEmail_shouldThrowWhenNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getByEmail(email));
    }

    @Test
    void success_delete() {
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.delete(userId);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteFail_notExistingUser_returnNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
    }

    @Test
    void success_update() {
        User user = new User();

        UserUpdateRequest updateRequest = new UserUpdateRequest();

        updateRequest.setEmail("new@example.com");
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("User");
        updateRequest.setFilled(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.update(userId, updateRequest);

        verify(userRepository).save(user);
    }

    @Test
    void updateFail_notExistingUser_returnNotFoundException() {
        UserUpdateRequest updateRequest = new UserUpdateRequest();

        updateRequest.setEmail("new@example.com");
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("User");
        updateRequest.setFilled(true);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(userId, updateRequest));
    }







}