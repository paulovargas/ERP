package com.paulovargas.erpapi.repository;

import com.paulovargas.erpapi.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByClientId(Long clientId);
}

