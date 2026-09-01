package com.paulovargas.erpapi.service;

import com.paulovargas.erpapi.dto.AddressRequest;
import com.paulovargas.erpapi.dto.AddressResponse;
import com.paulovargas.erpapi.entity.Address;
import com.paulovargas.erpapi.entity.Client;
import com.paulovargas.erpapi.exception.ResourceNotFoundException;
import com.paulovargas.erpapi.repository.AddressRepository;
import com.paulovargas.erpapi.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final ClientRepository clientRepository;

    public AddressService(AddressRepository addressRepository, ClientRepository clientRepository) {
        this.addressRepository = addressRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public AddressResponse create(Long clientId, AddressRequest request) {
        Client client = findClient(clientId);

        Address address = new Address();
        applyRequest(address, request);
        address.setClient(client);

        return toResponse(addressRepository.save(address));
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> findByClient(Long clientId) {
        findClient(clientId);

        return addressRepository.findByClientId(clientId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AddressResponse findById(Long id) {
        return toResponse(findAddress(id));
    }

    @Transactional
    public AddressResponse update(Long id, AddressRequest request) {
        Address address = findAddress(id);
        applyRequest(address, request);
        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public void delete(Long id) {
        addressRepository.delete(findAddress(id));
    }

    private Client findClient(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + clientId + "."));
    }

    private Address findAddress(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id " + id + "."));
    }

    private void applyRequest(Address address, AddressRequest request) {
        address.setZipCode(onlyDigits(request.getZipCode()));
        address.setStreet(trim(request.getStreet()));
        address.setNumber(trim(request.getNumber()));
        address.setComplement(trim(request.getComplement()));
        address.setNeighborhood(trim(request.getNeighborhood()));
        address.setCity(trim(request.getCity()));
        address.setState(trim(request.getState()).toUpperCase(Locale.ROOT));
    }

    private String onlyDigits(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private AddressResponse toResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setClientId(address.getClient().getId());
        response.setZipCode(address.getZipCode());
        response.setStreet(address.getStreet());
        response.setNumber(address.getNumber());
        response.setComplement(address.getComplement());
        response.setNeighborhood(address.getNeighborhood());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCreatedAt(address.getCreatedAt());
        response.setUpdatedAt(address.getUpdatedAt());
        return response;
    }
}

