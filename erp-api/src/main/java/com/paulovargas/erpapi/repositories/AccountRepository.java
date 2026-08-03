package com.paulovargas.erpapi.repositories;

import com.paulovargas.erpapi.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByNumberAccount(String numberAccount);
}
