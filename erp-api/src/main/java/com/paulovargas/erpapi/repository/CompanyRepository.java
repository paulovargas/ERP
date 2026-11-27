package com.paulovargas.erpapi.repository;

import com.paulovargas.erpapi.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByDocument(String document);
}
