package com.paulovargas.erpapi.service;

import com.paulovargas.erpapi.dto.ClientCreateRequest;
import com.paulovargas.erpapi.dto.ClientResponse;
import com.paulovargas.erpapi.dto.ClientUpdateRequest;
import com.paulovargas.erpapi.entity.Client;
import com.paulovargas.erpapi.entity.UserStatus;
import com.paulovargas.erpapi.entity.UserType;
import com.paulovargas.erpapi.exception.ResourceNotFoundException;
import com.paulovargas.erpapi.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ClientResponse create(ClientCreateRequest request) {
        String email = normalizeEmail(request.getEmail());
        String document = onlyDigits(request.getDocument());

        if (clientRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered.");
        }

        if (clientRepository.existsByDocument(document)) {
            throw new IllegalArgumentException("Document already registered.");
        }

        Client client = new Client();
        client.setName(trim(request.getName()));
        client.setTradeName(trim(request.getTradeName()));
        client.setEmail(email);
        client.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        client.setDocument(document);
        client.setPhone(onlyDigits(request.getPhone()));
        client.setUserType(defaultUserType(request.getUserType()));
        client.setStatus(UserStatus.ACTIVE);
        client.setSellerApproved(false);

        return toResponse(clientRepository.save(client));
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> findAll(UserStatus status, UserType userType) {
        List<Client> clients;

        if (status != null && userType != null) {
            clients = clientRepository.findByStatusAndUserType(status, userType);
        } else if (status != null) {
            clients = clientRepository.findByStatus(status);
        } else if (userType != null) {
            clients = clientRepository.findByUserType(userType);
        } else {
            clients = clientRepository.findAll();
        }

        return clients.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientResponse findById(Long id) {
        return toResponse(getClient(id));
    }

    @Transactional
    public ClientResponse update(Long id, ClientUpdateRequest request) {
        Client client = getClient(id);

        if (StringUtils.hasText(request.getName())) {
            client.setName(trim(request.getName()));
        }

        if (request.getTradeName() != null) {
            client.setTradeName(trim(request.getTradeName()));
        }

        if (StringUtils.hasText(request.getEmail())) {
            String email = normalizeEmail(request.getEmail());
            clientRepository.findByEmail(email)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("Email already registered.");
                    });
            client.setEmail(email);
        }

        if (StringUtils.hasText(request.getPassword())) {
            client.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        if (StringUtils.hasText(request.getDocument())) {
            String document = onlyDigits(request.getDocument());
            if (!document.equals(client.getDocument()) && clientRepository.existsByDocument(document)) {
                throw new IllegalArgumentException("Document already registered.");
            }
            client.setDocument(document);
        }

        if (request.getPhone() != null) {
            client.setPhone(onlyDigits(request.getPhone()));
        }

        if (request.getUserType() != null) {
            client.setUserType(request.getUserType());
            if (request.getUserType() == UserType.SELLER || request.getUserType() == UserType.BUYER_SELLER) {
                client.setSellerApproved(false);
            }
        }

        if (request.getStatus() != null) {
            client.setStatus(request.getStatus());
        }

        if (request.getSellerApproved() != null) {
            client.setSellerApproved(request.getSellerApproved());
        }

        return toResponse(clientRepository.save(client));
    }

    @Transactional
    public ClientResponse activate(Long id) {
        Client client = getClient(id);
        client.setStatus(UserStatus.ACTIVE);
        return toResponse(clientRepository.save(client));
    }

    @Transactional
    public ClientResponse deactivate(Long id) {
        Client client = getClient(id);
        client.setStatus(UserStatus.INACTIVE);
        return toResponse(clientRepository.save(client));
    }

    @Transactional
    public void delete(Long id) {
        Client client = getClient(id);
        clientRepository.delete(client);
    }

    private Client getClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + id + "."));
    }

    private UserType defaultUserType(UserType userType) {
        if (userType == null) {
            return UserType.BUYER;
        }

        if (userType == UserType.ADMIN) {
            throw new IllegalArgumentException("Admin users cannot be created through public registration.");
        }

        return userType;
    }

    private String normalizeEmail(String email) {
        return trim(email).toLowerCase(Locale.ROOT);
    }

    private String onlyDigits(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\\D", "");
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private ClientResponse toResponse(Client client) {
        ClientResponse response = new ClientResponse();
        response.setId(client.getId());
        response.setName(client.getName());
        response.setTradeName(client.getTradeName());
        response.setEmail(client.getEmail());
        response.setDocument(client.getDocument());
        response.setPhone(client.getPhone());
        response.setUserType(client.getUserType());
        response.setStatus(client.getStatus());
        response.setSellerApproved(client.isSellerApproved());
        response.setCreatedAt(client.getCreatedAt());
        response.setUpdatedAt(client.getUpdatedAt());
        return response;
    }
}

