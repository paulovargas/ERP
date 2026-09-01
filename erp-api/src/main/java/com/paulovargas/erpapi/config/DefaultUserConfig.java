package com.paulovargas.erpapi.config;

import com.paulovargas.erpapi.entity.Client;
import com.paulovargas.erpapi.entity.UserStatus;
import com.paulovargas.erpapi.entity.UserType;
import com.paulovargas.erpapi.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.Base64;

@Configuration
public class DefaultUserConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserConfig.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int DOCUMENT_LENGTH = 11;

    @Bean
    @Order(1)
    public CommandLineRunner createDefaultUser(
            ClientRepository clientRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.default-user.name:Administrador}") String name,
            @Value("${app.default-user.email:admin@admin.com}") String email,
            @Value("${app.default-user.password:}") String password,
            @Value("${app.default-user.document:00000000000}") String document,
            @Value("${app.default-user.phone:00000000000}") String phone
    ) {
        return args -> {
            String normalizedEmail = email.trim().toLowerCase();

            if (clientRepository.existsByEmail(normalizedEmail)) {
                LOGGER.info("Usuario administrador padrao ja existe: {}", normalizedEmail);
                return;
            }

            String resolvedPassword = StringUtils.hasText(password) ? password : generateTemporaryPassword();
            String resolvedDocument = resolveDocument(document, clientRepository);

            Client admin = new Client();
            admin.setName(name.trim());
            admin.setEmail(normalizedEmail);
            admin.setPasswordHash(passwordEncoder.encode(resolvedPassword));
            admin.setDocument(resolvedDocument);
            admin.setPhone(phone.replaceAll("\\D", ""));
            admin.setUserType(UserType.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setSellerApproved(true);

            clientRepository.save(admin);

            LOGGER.info("Usuario administrador padrao criado: {}", normalizedEmail);

            if (StringUtils.hasText(password)) {
                LOGGER.info("Senha do administrador padrao carregada da configuracao.");
            } else {
                LOGGER.warn("Senha temporaria do administrador: {}", resolvedPassword);
            }
        };
    }

    private String resolveDocument(String document, ClientRepository clientRepository) {
        String normalizedDocument = document.replaceAll("\\D", "");

        if (StringUtils.hasText(normalizedDocument) && !clientRepository.existsByDocument(normalizedDocument)) {
            return normalizedDocument;
        }

        String generatedDocument;
        do {
            generatedDocument = randomNumericString(DOCUMENT_LENGTH);
        } while (clientRepository.existsByDocument(generatedDocument));

        return generatedDocument;
    }

    private String generateTemporaryPassword() {
        byte[] bytes = new byte[18];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String randomNumericString(int length) {
        StringBuilder value = new StringBuilder(length);

        for (int index = 0; index < length; index++) {
            value.append(SECURE_RANDOM.nextInt(10));
        }

        return value.toString();
    }
}

