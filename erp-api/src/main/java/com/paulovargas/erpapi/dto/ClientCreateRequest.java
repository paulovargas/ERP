package com.paulovargas.erpapi.dto;

import com.paulovargas.erpapi.entity.UserType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ClientCreateRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 120)
    private String tradeName;

    @NotBlank
    @Email
    @Size(max = 160)
    private String email;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    @NotBlank
    @Pattern(regexp = "^[0-9.\\-/]{11,18}$", message = "Document must be a CPF or CNPJ.")
    private String document;

    @Pattern(regexp = "^[0-9()+\\-\\s]{10,20}$", message = "Phone must contain between 10 and 20 valid characters.")
    private String phone;

    private UserType userType;
}

