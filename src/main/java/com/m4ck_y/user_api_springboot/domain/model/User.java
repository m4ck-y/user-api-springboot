package com.m4ck_y.user_api_springboot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


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

}