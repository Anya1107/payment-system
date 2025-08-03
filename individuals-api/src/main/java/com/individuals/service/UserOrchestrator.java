package com.individuals.service;

import com.feign.client.UserServiceClient;
import com.individuals.dto.TokenResponse;
import com.individuals.dto.UserInfoResponse;
import com.individuals.dto.UserRegistrationRequest;
import com.individuals.exception.CustomAuthException;
import com.userservice.dto.AddressCreateRequest;
import com.userservice.dto.IndividualCreateRequest;
import com.userservice.dto.UserCreateRequest;
import com.userservice.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

import static com.individuals.util.Constants.USER_FAILED_DELETION_ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserOrchestrator {
    private final UserServiceClient userServiceClient;
    private final UserService userService;

    public Mono<TokenResponse> registerUser(UserRegistrationRequest request) {
        return Mono.fromCallable(() -> userServiceClient.createUser(mapToUserRequest(request)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(userUuid -> {
                    request.getUser().setId(userUuid);
                    return userService.register(request)
                            .onErrorResume(CustomAuthException.class, ex -> deleteDbUser(request, ex));
                });
    }

    public Mono<UserInfoResponse> getUserInfo(String accessToken) {
        return userService.extractUserId(accessToken)
                .flatMap(userIds ->
                        Mono.fromCallable(() -> userServiceClient.getUserById(UUID.fromString(userIds.getSecond())))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(user -> userService.getCurrentUser(userIds.getFirst()))
                );
    }

    public Mono<Void> updateUser(String accessToken, UserUpdateRequest userUpdateRequest) {
        return userService.extractUserId(accessToken)
                .flatMap(userIds ->
                        Mono.fromRunnable(() ->
                                        userServiceClient.updateUser(UUID.fromString(userIds.getSecond()), userUpdateRequest)
                                )
                                .subscribeOn(Schedulers.boundedElastic())
                                .then()
                );
    }

    public Mono<Void> deleteUser(String accessToken) {
        return userService.extractUserId(accessToken)
                .flatMap(userIds ->
                        Mono.fromRunnable(() ->
                                        userServiceClient.deleteUser(UUID.fromString(userIds.getSecond()))
                                )
                                .subscribeOn(Schedulers.boundedElastic())
                                .then(userService.deleteUser(userIds.getFirst()))
                );
    }

    private Mono<TokenResponse> deleteDbUser(UserRegistrationRequest request, CustomAuthException ex) {
        return Mono.fromRunnable(() -> {
            try {
                userServiceClient.deleteUser(request.getUser().getId());
            } catch (Exception e) {
                log.error(USER_FAILED_DELETION_ERROR_MESSAGE);
            }
        }).then(Mono.error(ex));
    }

    private com.userservice.dto.UserRegistrationRequest mapToUserRequest(UserRegistrationRequest request) {
        return new com.userservice.dto.UserRegistrationRequest()
                .user(
                        new UserCreateRequest()
                                .email(request.getUser().getEmail())
                                .firstName(request.getUser().getFirstName())
                                .lastName(request.getUser().getLastName())
                )
                .address(
                        new AddressCreateRequest()
                                .address(request.getAddress().getAddress())
                                .city(request.getAddress().getCity())
                                .state(request.getAddress().getState())
                                .zipCode(request.getAddress().getZipCode())
                                .countryId(request.getAddress().getCountryId())
                )
                .individual(
                        new IndividualCreateRequest()
                                .passportNumber(request.getIndividual().getPassportNumber())
                                .status(request.getIndividual().getStatus())
                                .phoneNumber(request.getIndividual().getPhoneNumber())
                );
    }
}
