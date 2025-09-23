package com.m4ck_y.user_api_springboot.application.service;

import com.m4ck_y.user_api_springboot.domain.dto.*;
import com.m4ck_y.user_api_springboot.domain.model.User;
import com.m4ck_y.user_api_springboot.domain.repository.UserRepository;
import com.m4ck_y.user_api_springboot.domain.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Value("${AES_KEY}")
    private String aesKey; // es mejor no exponer la clave en el código, se almacena en variables de entorno, .env

    private static final String ALGORITHM = "AES";

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDTO> getUsers(String sortedBy, String filter) {
        List<User> users = userRepository.findAll();

        if (filter != null && !filter.trim().isEmpty()) {
            users = applyFilter(users, filter);
        }

        if (sortedBy != null && !sortedBy.trim().isEmpty()) {
            users = applySorting(users, sortedBy);
        }

        return users.stream().map(User::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO createUser(CreateUserRequest request) {

        if (userRepository.existsByTaxId(request.getTaxId())) {
            throw new IllegalArgumentException("Tax ID already exists");
        }

        String encryptedPassword = encryptPassword(request.getPassword());

        String createdAt = getCurrentTimeInMadagascar();

        User user = User.fromCreateRequest(request);
        user.setPassword(encryptedPassword);
        user.setCreatedAt(createdAt);

        User savedUser = userRepository.save(user);
        return User.toDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(UUID id, UpdateUserRequest request) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User existingUser = existingUserOpt.get();

        // Verificar que el taxId(RFC) no esté asignado a otro usuario diferente
        if (request.getTaxId() != null && !request.getTaxId().equals(existingUser.getTaxId()) && userRepository.existsByTaxId(request.getTaxId())) {
            throw new IllegalArgumentException("Tax ID already exists");
        }

        User.fromUpdateRequest(id, request, existingUser);

        if (request.getPassword() != null) {
            existingUser.setPassword(encryptPassword(request.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return User.toDTO(updatedUser);
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public boolean authenticate(String taxId, String password) {
        Optional<User> userOpt = userRepository.findByTaxId(taxId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String encryptedPassword = encryptPassword(password);
            return encryptedPassword.equals(user.getPassword());
        }
        return false;
    }

    private List<User> applyFilter(List<User> users, String filter) {
        
        String[] parts = filter.split("\\+");
        if (parts.length != 3) {
            return users;
        }
        String attribute = parts[0];
        String operator = parts[1];
        String value = parts[2];

        return users.stream().filter(user -> matchesFilter(user, attribute, operator, value)).collect(Collectors.toList());
    }

    private boolean matchesFilter(User user, String attribute, String operator, String value) {
        String fieldValue = getFieldValue(user, attribute);
        if (fieldValue == null) return false;

        switch (operator) {
            case "eq": return fieldValue.equals(value);
            case "co": return fieldValue.contains(value);
            case "sw": return fieldValue.startsWith(value);
            case "ew": return fieldValue.endsWith(value);
            default: return false;
        }
    }

    private String getFieldValue(User user, String attribute) {
        switch (attribute) {
            case "id": return user.getId().toString();
            case "email": return user.getEmail();
            case "name": return user.getName();
            case "phone": return user.getPhone();
            case "tax_id": return user.getTaxId();
            case "created_at": return user.getCreatedAt();
            default: return null;
        }
    }

    private List<User> applySorting(List<User> users, String sortedBy) {
        Comparator<User> comparator = getComparator(sortedBy);
        return users.stream().sorted(comparator).collect(Collectors.toList());
    }

    private Comparator<User> getComparator(String sortedBy) {
        switch (sortedBy) {
            case "id": return Comparator.comparing(User::getId);
            case "email": return Comparator.comparing(User::getEmail);
            case "name": return Comparator.comparing(User::getName);
            case "phone": return Comparator.comparing(User::getPhone);
            case "tax_id": return Comparator.comparing(User::getTaxId);
            case "created_at": return Comparator.comparing(User::getCreatedAt);
            default: return Comparator.comparing(User::getId);
        }
    }


    String encryptPassword(String password) {
        try {
            SecretKeySpec key = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting password", e);
        }
    }

    private String getCurrentTimeInMadagascar() {
        // ZOna horaria, fuente: https://www.zeitverschiebung.net/es/country/mg
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Indian/Antananarivo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return now.format(formatter);
    }

}