package com.userservice.service;

import com.userservice.dto.IndividualCreateRequest;
import com.userservice.entity.Individual;
import com.userservice.entity.User;

public interface IndividualService {

    Individual create(IndividualCreateRequest individualCreateRequest, User user);
}
