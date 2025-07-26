package com.userservice.service;

import com.userservice.entity.Address;
import com.userservice.entity.Country;
import com.userservice.entity.Individual;
import com.userservice.entity.User;
import com.userservice.exception.UserAlreadyExistsException;
import com.userservice.exception.UserNotFoundException;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.UserRepository;
import com.userservice.request.UserRegistrationRequest;
import com.userservice.request.UserUpdateRequest;
import com.userservice.response.UserDto;
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
    public void register(UserRegistrationRequest userRegistrationRequest) {
        validateUserExistence(userRegistrationRequest);

        Address address = addressService.create(userRegistrationRequest.address());

        User user = createUser(userRegistrationRequest, address);

        individualService.create(userRegistrationRequest.individual(), user);
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
        String email = userRegistrationRequest.user().email();

        boolean exists = userRepository.existsByEmail(email);

        if (exists) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS_ERROR_MESSAGE + email, CONFLICT_STATUS_CODE);
        }
    }

    private User createUser(UserRegistrationRequest userRegistrationRequest, Address address) {
        User user = new User();

        user.setEmail(userRegistrationRequest.user().email());
        user.setSecretKey(userRegistrationRequest.user().secretKey());
        user.setFirstName(userRegistrationRequest.user().firstName());
        user.setLastName(userRegistrationRequest.user().lastName());
        user.setFilled(true);
        user.setAddress(address);
        user = userRepository.save(user);

        return user;
    }

    private void fillUser(UserUpdateRequest userUpdateRequest, User user) {
        if (userUpdateRequest.email() != null) user.setEmail(userUpdateRequest.email());
        if (userUpdateRequest.firstName() != null) user.setFirstName(userUpdateRequest.firstName());
        if (userUpdateRequest.lastName() != null) user.setLastName(userUpdateRequest.lastName());
        if (userUpdateRequest.filled() != null) user.setFilled(userUpdateRequest.filled());
    }

    private void fillIndividual(UserUpdateRequest userUpdateRequest, User user) {
        if (userUpdateRequest.individual() != null) {
            Individual individual = getIndividual(user);
            if (userUpdateRequest.individual().passportNumber() != null)
                individual.setPassportNumber(userUpdateRequest.individual().passportNumber());
            if (userUpdateRequest.individual().phoneNumber() != null)
                individual.setPhoneNumber(userUpdateRequest.individual().phoneNumber());
            if (userUpdateRequest.individual().status() != null)
                individual.setStatus(userUpdateRequest.individual().status());
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
        if (userUpdateRequest.address() != null) {
            Address address = getAddress(user);
            if (userUpdateRequest.address().address() != null)
                address.setAddress(userUpdateRequest.address().address());
            if (userUpdateRequest.address().city() != null)
                address.setCity(userUpdateRequest.address().city());
            if (userUpdateRequest.address().state() != null)
                address.setState(userUpdateRequest.address().state());
            if (userUpdateRequest.address().zipCode() != null)
                address.setZipCode(userUpdateRequest.address().zipCode());
            if (userUpdateRequest.address().countryId() != null) {
                Country country = countryService.getCountry(userUpdateRequest.address().countryId());
                address.setCountry(country);
            }
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
