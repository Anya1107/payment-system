package com.userservice.service;

import com.userservice.entity.Address;
import com.userservice.request.AddressCreateRequest;

public interface AddressService {

    Address create(AddressCreateRequest addressCreateRequest);
}
