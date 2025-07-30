package com.userservice.service;

import com.userservice.dto.AddressCreateRequest;
import com.userservice.entity.Address;

public interface AddressService {

    Address create(AddressCreateRequest addressCreateRequest);
}
