package com.paulovargas.erpapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZipCodeResponse {

    private String zipCode;

    private String street;

    private String complement;

    private String neighborhood;

    private String city;

    private String state;
}

