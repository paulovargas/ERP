package com.paulovargas.erpapi.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class MenuRequest {

    @NotBlank(message = "Name is required.")
    @Size(max = 120, message = "Name must have at most 120 characters.")
    private String name;

    @Size(max = 160, message = "Address must have at most 160 characters.")
    private String address;

    private Long position;

    private Long root;
}

