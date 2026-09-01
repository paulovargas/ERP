package com.paulovargas.erpapi.service;

import com.paulovargas.erpapi.dto.MenuRequest;
import com.paulovargas.erpapi.dto.MenuResponse;
import com.paulovargas.erpapi.dto.UserMenuAccessRequest;
import com.paulovargas.erpapi.entity.Client;
import com.paulovargas.erpapi.entity.Menu;
import com.paulovargas.erpapi.entity.UserType;
import com.paulovargas.erpapi.exception.ResourceNotFoundException;
import com.paulovargas.erpapi.repository.ClientRepository;
import com.paulovargas.erpapi.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final ClientRepository clientRepository;

    public MenuService(MenuRepository menuRepository, ClientRepository clientRepository) {
        this.menuRepository = menuRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public MenuResponse create(MenuRequest request) {
        Menu menu = new Menu();
        apply(menu, request);
        return toResponse(menuRepository.save(menu));
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> findAll() {
        return menuRepository.findAllByOrderByRootAscPositionAscNameAsc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MenuResponse findById(Long id) {
        return toResponse(getMenu(id));
    }

    @Transactional
    public MenuResponse update(Long id, MenuRequest request) {
        Menu menu = getMenu(id);
        apply(menu, request);
        return toResponse(menuRepository.save(menu));
    }

    @Transactional
    public void delete(Long id) {
        Menu menu = getMenu(id);
        menu.getClients().forEach(client -> client.getMenus().remove(menu));
        menuRepository.delete(menu);
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> findByClientId(Long clientId) {
        getClient(clientId);
        return menuRepository.findByClientId(clientId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> findByAuthenticatedUser(String email) {
        Client client = clientRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));

        if (client.getUserType() == UserType.ADMIN) {
            return findAll();
        }

        return menuRepository.findByClientId(client.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MenuResponse> updateUserAccess(Long clientId, UserMenuAccessRequest request) {
        Client client = getClient(clientId);
        Set<Menu> menus = request.getMenuIds()
                .stream()
                .map(this::getMenu)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        client.setMenus(menus);
        clientRepository.save(client);

        return menuRepository.findByClientId(clientId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void apply(Menu menu, MenuRequest request) {
        menu.setName(trimRequired(request.getName()));
        menu.setAddress(trim(request.getAddress()));
        menu.setPosition(request.getPosition());
        menu.setRoot(request.getRoot());
    }

    private Menu getMenu(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id " + id + "."));
    }

    private Client getClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + id + "."));
    }

    private MenuResponse toResponse(Menu menu) {
        MenuResponse response = new MenuResponse();
        response.setId(menu.getId());
        response.setName(menu.getName());
        response.setAddress(menu.getAddress());
        response.setPosition(menu.getPosition());
        response.setRoot(menu.getRoot());
        return response;
    }

    private String normalizeEmail(String email) {
        return trimRequired(email).toLowerCase(Locale.ROOT);
    }

    private String trimRequired(String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("Required value cannot be blank.");
        }
        return value.trim();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}

