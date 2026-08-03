package com.paulovargas.erpapi.repositories;

import com.paulovargas.erpapi.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {
}
