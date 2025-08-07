package com.userservice.service;

import com.userservice.entity.Country;
import com.userservice.exception.CountryNotFoundException;
import com.userservice.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.userservice.util.Constants.COUNTRY_NOT_FOUND_ERROR_MESSAGE;
import static com.userservice.util.Constants.NOT_FOUND_STATUS_CODE;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public Country getCountry(Integer id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException(COUNTRY_NOT_FOUND_ERROR_MESSAGE, NOT_FOUND_STATUS_CODE));
    }
}
