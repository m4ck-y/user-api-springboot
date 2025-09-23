package com.m4ck_y.user_api_springboot.domain.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserDTO {

    private UUID id;

    private String email;

    private String name;

    private String phone;

    private String taxId;

    private String createdAt;

    private List<AddressDTO> addresses;

}