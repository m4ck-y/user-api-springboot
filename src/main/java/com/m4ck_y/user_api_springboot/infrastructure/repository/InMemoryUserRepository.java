package com.m4ck_y.user_api_springboot.infrastructure.repository;

import com.m4ck_y.user_api_springboot.domain.model.Address;
import com.m4ck_y.user_api_springboot.domain.model.User;
import com.m4ck_y.user_api_springboot.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        initializeUsers();
    }

    private void initializeUsers() {
        UUID id1 = UUID.randomUUID();
        List<Address> addresses1 = new ArrayList<>(List.of(
                new Address(UUID.randomUUID(), "workaddress", "street No. 1", "UK"),
                new Address(UUID.randomUUID(), "homeaddress", "street No. 2", "AU")
        ));
        User user1 = new User(id1, "user1@mail.com", "user1", "+1 55 555 555 55",
                "7c4a8d09ca3762af61e59520943dc26494f8941b", "AARR990101XXX",
                "01-01-2024 00:00:00", addresses1);
        users.put(id1, user1);

        UUID id2 = UUID.randomUUID();
        List<Address> addresses2 = new ArrayList<>(List.of(
                new Address(UUID.randomUUID(), "workaddress", "street No. 1", "UK"),
                new Address(UUID.randomUUID(), "homeaddress", "street No. 2", "AU")
        ));
        User user2 = new User(id2, "user2@mail.com", "user2", "+1 55 555 555 55",
                "7c4a8d09ca3762af61e59520943dc26494f8941b", "AARR990101XXX",
                "01-01-2024 00:00:00", addresses2);
        users.put(id2, user2);

        UUID id3 = UUID.randomUUID();
        List<Address> addresses3 = new ArrayList<>(List.of(
                new Address(UUID.randomUUID(), "workaddress", "street No. 1", "UK"),
                new Address(UUID.randomUUID(), "homeaddress", "street No. 2", "AU")
        ));
        User user3 = new User(id3, "user3@mail.com", "user3", "+1 55 555 555 55",
                "7c4a8d09ca3762af61e59520943dc26494f8941b", "AARR990101XXX",
                "01-01-2024 00:00:00", addresses3);
        users.put(id3, user3);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByTaxId(String taxId) {
        return users.values().stream()
                .filter(user -> taxId.equals(user.getTaxId()))
                .findFirst();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(UUID id) {
        users.remove(id);
    }

    @Override
    public boolean existsByTaxId(String taxId) {
        return users.values().stream()
                .anyMatch(user -> taxId.equals(user.getTaxId()));
    }
}