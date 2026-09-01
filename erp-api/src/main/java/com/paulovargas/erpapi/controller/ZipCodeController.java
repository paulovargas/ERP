package com.paulovargas.erpapi.controller;

import com.paulovargas.erpapi.dto.ZipCodeResponse;
import com.paulovargas.erpapi.service.ZipCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/zip-codes")
public class ZipCodeController {

    private final ZipCodeService zipCodeService;

    public ZipCodeController(ZipCodeService zipCodeService) {
        this.zipCodeService = zipCodeService;
    }

    @GetMapping("/{zipCode}")
    public ResponseEntity<ZipCodeResponse> findByZipCode(@PathVariable String zipCode) {
        return ResponseEntity.ok(zipCodeService.findByZipCode(zipCode));
    }
}

