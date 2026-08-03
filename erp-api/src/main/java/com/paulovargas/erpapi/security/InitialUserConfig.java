package com.paulovargas.erpapi.security;

import com.paulovargas.erpapi.entities.AppUser;
import com.paulovargas.erpapi.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InitialUserConfig {
    @Bean
    public CommandLineRunner createInitialUser(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.initial-user.username:}") String username,
            @Value("${app.security.initial-user.password:}") String password
    ) {
        return args -> {
            if (username.isBlank() || password.isBlank() || appUserRepository.existsByUsername(username)) {
                return;
            }

            AppUser user = new AppUser();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("ADMIN");
            appUserRepository.save(user);
        };
    }
}
