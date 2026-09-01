package com.paulovargas.erpapi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "address", length = 160)
    private String address;

    @Column
    private Long position;

    @Column
    private Long root;

    @ManyToMany(mappedBy = "menus")
    private Set<Client> clients = new LinkedHashSet<>();
}

