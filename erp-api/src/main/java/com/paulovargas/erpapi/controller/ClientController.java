package com.paulovargas.erpapi.controller;

import com.paulovargas.erpapi.dto.ClientCreateRequest;
import com.paulovargas.erpapi.dto.ClientResponse;
import com.paulovargas.erpapi.dto.ClientUpdateRequest;
import com.paulovargas.erpapi.entity.UserStatus;
import com.paulovargas.erpapi.entity.UserType;
import com.paulovargas.erpapi.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping({"/api/clients", "/api/users"})
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> findAll(
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) UserType userType
    ) {
        return ResponseEntity.ok(clientService.findAll(status, userType));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ClientUpdateRequest request
    ) {
        return ResponseEntity.ok(clientService.update(id, request));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ClientResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ClientResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.deactivate(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

