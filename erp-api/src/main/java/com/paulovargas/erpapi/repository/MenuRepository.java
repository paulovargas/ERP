package com.paulovargas.erpapi.repository;

import com.paulovargas.erpapi.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllByOrderByRootAscPositionAscNameAsc();

    @Query("select m from Menu m where m.name = :name and ((:address is null and m.address is null) or m.address = :address)")
    Optional<Menu> findByNameAndAddress(@Param("name") String name, @Param("address") String address);

    @Query("select m from Client c join c.menus m where c.id = :clientId order by m.root asc, m.position asc, m.name asc")
    List<Menu> findByClientId(@Param("clientId") Long clientId);
}

