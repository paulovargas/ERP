package com.paulovargas.erpapi.security;

import com.paulovargas.erpapi.entity.Client;
import com.paulovargas.erpapi.entity.UserStatus;
import com.paulovargas.erpapi.repository.ClientRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Locale;

@Service
public class ClientUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;

    public ClientUserDetailsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = username.trim().toLowerCase(Locale.ROOT);

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

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
}

