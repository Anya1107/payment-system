package com.userservice.service;

import com.userservice.dto.UserDto;
import com.userservice.dto.UserRegistrationRequest;
import com.userservice.dto.UserUpdateRequest;
import com.userservice.entity.Address;
import com.userservice.entity.Country;
import com.userservice.entity.Individual;
import com.userservice.entity.User;
import com.userservice.exception.UserAlreadyExistsException;
import com.userservice.exception.UserNotFoundException;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.userservice.util.Constants.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AddressService addressService;
    private final IndividualService individualService;
    private final UserMapper userMapper;
    private final CountryService countryService;

    @Override
    @Transactional
    public UUID register(UserRegistrationRequest userRegistrationRequest) {
        validateUserExistence(userRegistrationRequest);

        Address address = addressService.create(userRegistrationRequest.getAddress());

        User user = createUser(userRegistrationRequest, address);

        individualService.create(userRegistrationRequest.getIndividual(), user);

        return user.getId();
    }

    @Override
    public UserDto getById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(USER_WITH_ID_NOT_FOUND_ERROR_MESSAGE + id, NOT_FOUND_STATUS_CODE));
    }

    @Override
    public UserDto getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(USER_WITH_EMAIL_NOT_FOUND_ERROR_MESSAGE + email, NOT_FOUND_STATUS_CODE));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_WITH_ID_NOT_FOUND_ERROR_MESSAGE + id, NOT_FOUND_STATUS_CODE));

        userRepository.delete(user);
    }

    @Override
    public void update(UUID id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_WITH_ID_NOT_FOUND_ERROR_MESSAGE + id, NOT_FOUND_STATUS_CODE));

        fillUser(userUpdateRequest, user);
        fillIndividual(userUpdateRequest, user);
        fillAddress(userUpdateRequest, user);

        userRepository.save(user);
    }

    private void validateUserExistence(UserRegistrationRequest userRegistrationRequest) {
        String email = userRegistrationRequest.getUser().getEmail();

        boolean exists = userRepository.existsByEmail(email);

        if (exists) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS_ERROR_MESSAGE + email, CONFLICT_STATUS_CODE);
        }
    }

    private User createUser(UserRegistrationRequest userRegistrationRequest, Address address) {
        User user = new User();

        user.setEmail(userRegistrationRequest.getUser().getEmail());
        user.setSecretKey(userRegistrationRequest.getUser().getSecretKey());
        user.setFirstName(userRegistrationRequest.getUser().getFirstName());
        user.setLastName(userRegistrationRequest.getUser().getLastName());
        user.setFilled(true);
        user.setAddress(address);
        user = userRepository.save(user);

        return user;
    }

    private void fillUser(UserUpdateRequest userUpdateRequest, User user) {
        user.setEmail(userUpdateRequest.getEmail());
        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());
        user.setFilled(userUpdateRequest.getFilled());
    }

    private void fillIndividual(UserUpdateRequest userUpdateRequest, User user) {
        if (userUpdateRequest.getIndividual() != null) {
            Individual individual = getIndividual(user);
            individual.setPassportNumber(userUpdateRequest.getIndividual().getPassportNumber());
            individual.setPhoneNumber(userUpdateRequest.getIndividual().getPhoneNumber());
            individual.setStatus(userUpdateRequest.getIndividual().getStatus());
        }
    }

    private Individual getIndividual(User user) {
        Individual individual = user.getIndividual();

        if (individual == null) {
            individual = new Individual();
            individual.setUser(user);
            user.setIndividual(individual);
        }

        return individual;
    }

    private void fillAddress(UserUpdateRequest userUpdateRequest, User user) {
        if (userUpdateRequest.getAddress() != null) {
            Address address = getAddress(user);
            Country country = countryService.getCountry(userUpdateRequest.getAddress().getCountryId());

            address.setAddress(userUpdateRequest.getAddress().getAddress());
            address.setCity(userUpdateRequest.getAddress().getCity());
            address.setState(userUpdateRequest.getAddress().getState());
            address.setZipCode(userUpdateRequest.getAddress().getZipCode());
            address.setCountry(country);
        }
    }

    private Address getAddress(User user) {
        Address address = user.getAddress();

        if (address == null) {
            address = new Address();
            user.setAddress(address);
        }

        return address;
    }
}
