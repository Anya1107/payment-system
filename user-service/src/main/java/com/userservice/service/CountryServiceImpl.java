package com.userservice.service;

import com.userservice.entity.Country;
import com.userservice.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public Country getCountry(Integer id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Country not found"));
    }
}
