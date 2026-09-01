package com.paulovargas.erpapi.dto;

import com.paulovargas.erpapi.entity.UserStatus;
import com.paulovargas.erpapi.entity.UserType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ClientResponse {

    private Long id;

    private String name;

    private String tradeName;

    private String email;

    private String document;

    private String phone;

    private UserType userType;

    private UserStatus status;

    private boolean sellerApproved;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

