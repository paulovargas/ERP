package com.paulovargas.erpapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddressResponse {

    private Long id;

    private Long clientId;

    private String zipCode;

    private String street;

    private String number;

    private String complement;

    private String neighborhood;

    private String city;

    private String state;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

