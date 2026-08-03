package com.paulovargas.erpapi.repositories;

import com.paulovargas.erpapi.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
