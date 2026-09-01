package com.paulovargas.erpapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuResponse {

    private Long id;

    private String name;

    private String address;

    private Long position;

    private Long root;
}

