package com.paulovargas.erpapi.security;

import com.paulovargas.erpapi.entities.AppUser;
import com.paulovargas.erpapi.entity.Client;
import com.paulovargas.erpapi.entity.UserStatus;
import com.paulovargas.erpapi.repositories.AppUserRepository;
import com.paulovargas.erpapi.repository.ClientRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Locale;

public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository appUserRepository;
    private final ClientRepository clientRepository;

    public CustomUserDetailsService(AppUserRepository appUserRepository, ClientRepository clientRepository) {
        this.appUserRepository = appUserRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return clientRepository.findByEmail(username.trim().toLowerCase(Locale.ROOT))
                .map(this::toClientUser)
                .orElseGet(() -> toAppUser(username));
    }

    private UserDetails toClientUser(Client client) {
        return new User(
                client.getEmail(),
                client.getPasswordHash(),
                client.getStatus() == UserStatus.ACTIVE,
                true,
                true,
                client.getStatus() != UserStatus.BLOCKED,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + client.getUserType().name()))
        );
    }

    private UserDetails toAppUser(String username) {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + appUser.getRole()))
        );
    }
}
