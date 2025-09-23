package com.m4ck_y.user_api_springboot.domain.service;

import com.m4ck_y.user_api_springboot.domain.dto.CreateUserRequest;
import com.m4ck_y.user_api_springboot.domain.dto.UpdateUserRequest;
import com.m4ck_y.user_api_springboot.domain.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserDTO> getUsers(String sortedBy, String filter);

    UserDTO createUser(CreateUserRequest request);

    UserDTO updateUser(UUID id, UpdateUserRequest request);

    void deleteUser(UUID id);

    boolean authenticate(String taxId, String password);

}