package com.m4ck_y.user_api_springboot.domain.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private UUID id;

    private String name;

    private String street;

    private String countryCode;

}