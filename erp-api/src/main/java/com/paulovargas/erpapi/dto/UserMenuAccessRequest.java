package com.paulovargas.erpapi.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class UserMenuAccessRequest {

    @NotNull(message = "Menu ids are required.")
    private Set<Long> menuIds = new LinkedHashSet<>();
}

