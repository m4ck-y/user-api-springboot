package com.m4ck_y.user_api_springboot.domain.repository;

import com.m4ck_y.user_api_springboot.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(UUID id);

    Optional<User> findByTaxId(String taxId);

    User save(User user);

    void deleteById(UUID id);

    boolean existsByTaxId(String taxId);

}