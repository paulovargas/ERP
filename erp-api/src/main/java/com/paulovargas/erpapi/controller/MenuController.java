package com.paulovargas.erpapi.controller;

import com.paulovargas.erpapi.dto.MenuRequest;
import com.paulovargas.erpapi.dto.MenuResponse;
import com.paulovargas.erpapi.dto.UserMenuAccessRequest;
import com.paulovargas.erpapi.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/menus")
@Tag(name = "Menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    @Operation(summary = "Cria um menu")
    public ResponseEntity<MenuResponse> create(@Valid @RequestBody MenuRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.create(request));
    }

    @GetMapping
    @Operation(summary = "Lista todos os menus")
    public ResponseEntity<List<MenuResponse>> findAll() {
        return ResponseEntity.ok(menuService.findAll());
    }

    @GetMapping("/me")
    @Operation(summary = "Lista os menus permitidos para o usuario autenticado")
    public ResponseEntity<List<MenuResponse>> findMyMenus(Authentication authentication) {
        return ResponseEntity.ok(menuService.findByAuthenticatedUser(authentication.getName()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um menu por id")
    public ResponseEntity<MenuResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um menu")
    public ResponseEntity<MenuResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MenuRequest request
    ) {
        return ResponseEntity.ok(menuService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um menu")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{clientId}")
    @Operation(summary = "Lista os menus permitidos para um usuario")
    public ResponseEntity<List<MenuResponse>> findByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(menuService.findByClientId(clientId));
    }

    @PutMapping("/users/{clientId}")
    @Operation(summary = "Atualiza os menus permitidos para um usuario")
    public ResponseEntity<List<MenuResponse>> updateUserAccess(
            @PathVariable Long clientId,
            @Valid @RequestBody UserMenuAccessRequest request
    ) {
        return ResponseEntity.ok(menuService.updateUserAccess(clientId, request));
    }
}

