package com.m4ck_y.user_api_springboot.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {

    @NotBlank
    private String taxId;

    @NotBlank
    private String password;

}