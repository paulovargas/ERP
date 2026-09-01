package com.paulovargas.erpapi.controller;

import com.paulovargas.erpapi.dto.AddressRequest;
import com.paulovargas.erpapi.dto.AddressResponse;
import com.paulovargas.erpapi.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/api/clients/{clientId}/addresses")
    public ResponseEntity<AddressResponse> create(
            @PathVariable Long clientId,
            @Valid @RequestBody AddressRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(clientId, request));
    }

    @GetMapping("/api/clients/{clientId}/addresses")
    public ResponseEntity<List<AddressResponse>> findByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(addressService.findByClient(clientId));
    }

    @GetMapping("/api/addresses/{id}")
    public ResponseEntity<AddressResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.findById(id));
    }

    @PutMapping("/api/addresses/{id}")
    public ResponseEntity<AddressResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request
    ) {
        return ResponseEntity.ok(addressService.update(id, request));
    }

    @DeleteMapping("/api/addresses/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

