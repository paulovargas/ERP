package com.paulovargas.erpapi.repository;

import com.paulovargas.erpapi.entity.Client;
import com.paulovargas.erpapi.entity.UserStatus;
import com.paulovargas.erpapi.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByDocument(String document);

    List<Client> findByStatus(UserStatus status);

    List<Client> findByUserType(UserType userType);

    List<Client> findByStatusAndUserType(UserStatus status, UserType userType);
}

