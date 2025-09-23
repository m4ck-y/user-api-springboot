package com.m4ck_y.user_api_springboot.domain.model;

import com.m4ck_y.user_api_springboot.domain.dto.AddressDTO;
import com.m4ck_y.user_api_springboot.domain.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;

    private String email;

    private String name;

    private String phone;

    private String password;

    private String taxId;

    private String createdAt;

    private List<Address> addresses;

    public static UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setTaxId(user.getTaxId());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setAddresses(user.getAddresses() != null ?
            user.getAddresses().stream().map(Address::toDTO).collect(Collectors.toList()) :
            null);
        return dto;
    }

    public static User fromCreateRequest(com.m4ck_y.user_api_springboot.domain.dto.CreateUserRequest request) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        user.setTaxId(request.getTaxId());
        user.setAddresses(request.getAddresses() != null ?
            request.getAddresses().stream().map(Address::fromDTO).collect(Collectors.toList()) :
            null);
        return user;
    }

    public static User fromUpdateRequest(UUID id, com.m4ck_y.user_api_springboot.domain.dto.UpdateUserRequest request, User existing) {
        existing.setEmail(request.getEmail() != null ? request.getEmail() : existing.getEmail());
        existing.setName(request.getName() != null ? request.getName() : existing.getName());
        existing.setPhone(request.getPhone() != null ? request.getPhone() : existing.getPhone());
        existing.setPassword(request.getPassword() != null ? request.getPassword() : existing.getPassword());
        existing.setTaxId(request.getTaxId() != null ? request.getTaxId() : existing.getTaxId());
        existing.setAddresses(request.getAddresses() != null ?
            request.getAddresses().stream().map(Address::fromDTO).collect(Collectors.toList()) :
            existing.getAddresses());
        return existing;
    }
}