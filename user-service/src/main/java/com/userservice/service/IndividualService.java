package com.userservice.service;

import com.userservice.entity.Individual;
import com.userservice.entity.User;
import com.userservice.request.IndividualCreateRequest;

public interface IndividualService {

    Individual create(IndividualCreateRequest individualCreateRequest, User user);
}
