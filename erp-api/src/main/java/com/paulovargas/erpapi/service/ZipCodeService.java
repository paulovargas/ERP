package com.paulovargas.erpapi.service;

import com.paulovargas.erpapi.dto.ViaCepResponse;
import com.paulovargas.erpapi.dto.ZipCodeResponse;
import com.paulovargas.erpapi.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ZipCodeService {

    private final RestTemplate restTemplate;

    public ZipCodeService() {
        this.restTemplate = new RestTemplate();
    }

    public ZipCodeResponse findByZipCode(String zipCode) {
        String normalizedZipCode = onlyDigits(zipCode);

        if (normalizedZipCode.length() != 8) {
            throw new IllegalArgumentException("Zip code must contain 8 digits.");
        }

        ViaCepResponse viaCepResponse = fetchFromViaCep(normalizedZipCode);

        if (viaCepResponse == null || viaCepResponse.isError()) {
            throw new ResourceNotFoundException("Zip code not found: " + normalizedZipCode + ".");
        }

        ZipCodeResponse response = new ZipCodeResponse();
        response.setZipCode(onlyDigits(viaCepResponse.getCep()));
        response.setStreet(viaCepResponse.getLogradouro());
        response.setComplement(viaCepResponse.getComplemento());
        response.setNeighborhood(viaCepResponse.getBairro());
        response.setCity(viaCepResponse.getLocalidade());
        response.setState(viaCepResponse.getUf());
        return response;
    }

    private ViaCepResponse fetchFromViaCep(String zipCode) {
        try {
            return restTemplate.getForObject(
                    "https://viacep.com.br/ws/{zipCode}/json/",
                    ViaCepResponse.class,
                    zipCode
            );
        } catch (RestClientException exception) {
            throw new IllegalArgumentException("Could not consult zip code service.");
        }
    }

    private String onlyDigits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }
}

