package com.userservice.service;

import com.userservice.dto.AddressCreateRequest;
import com.userservice.entity.Address;
import com.userservice.entity.Country;
import com.userservice.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final CountryService countryService;

    @Override
    public Address create(AddressCreateRequest addressCreateRequest) {
        Country country = countryService.getCountry(addressCreateRequest.getCountryId());

        Address address = new Address();
        address.setAddress(addressCreateRequest.getAddress());
        address.setZipCode(addressCreateRequest.getZipCode());
        address.setCity(addressCreateRequest.getCity());
        address.setState(addressCreateRequest.getState());
        address.setCountry(country);

        return addressRepository.save(address);
    }
}
