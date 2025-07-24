package com.userservice.service;

import com.userservice.entity.Address;
import com.userservice.entity.Country;
import com.userservice.repository.AddressRepository;
import com.userservice.request.AddressCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final CountryService countryService;

    @Override
    public Address create(AddressCreateRequest addressCreateRequest) {
        Country country = countryService.getCountry(addressCreateRequest.countryId());

        Address address = new Address();
        address.setAddress(addressCreateRequest.address());
        address.setZipCode(addressCreateRequest.zipCode());
        address.setCity(addressCreateRequest.city());
        address.setState(addressCreateRequest.state());
        address.setCountry(country);

        return addressRepository.save(address);
    }
}
