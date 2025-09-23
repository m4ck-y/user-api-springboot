package com.m4ck_y.user_api_springboot.domain.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AddressDTO {

    private UUID id;

    private String name;

    private String street;

    private String countryCode;

}