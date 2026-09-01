package com.paulovargas.erpapi.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class AddressRequest {

    @NotBlank
    @Pattern(regexp = "^[0-9]{5}-?[0-9]{3}$", message = "Zip code must be a valid CEP.")
    private String zipCode;

    @NotBlank
    @Size(max = 160)
    private String street;

    @NotBlank
    @Size(max = 20)
    private String number;

    @Size(max = 120)
    private String complement;

    @NotBlank
    @Size(max = 120)
    private String neighborhood;

    @NotBlank
    @Size(max = 120)
    private String city;

    @NotBlank
    @Size(min = 2, max = 2)
    private String state;
}

