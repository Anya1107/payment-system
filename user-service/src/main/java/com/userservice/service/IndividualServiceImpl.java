package com.userservice.service;

import com.userservice.dto.IndividualCreateRequest;
import com.userservice.entity.Individual;
import com.userservice.entity.User;
import com.userservice.repository.IndividualRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndividualServiceImpl implements IndividualService {
    private final IndividualRepository individualRepository;

    @Override
    public Individual create(IndividualCreateRequest individualCreateRequest, User user) {
        Individual individual = new Individual();
        individual.setUser(user);
        individual.setPassportNumber(individualCreateRequest.getPassportNumber());
        individual.setPhoneNumber(individualCreateRequest.getPhoneNumber());
        individual.setStatus(individualCreateRequest.getStatus());

        return individualRepository.save(individual);
    }
}
