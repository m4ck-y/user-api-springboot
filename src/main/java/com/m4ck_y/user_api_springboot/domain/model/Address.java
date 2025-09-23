package com.m4ck_y.user_api_springboot.domain.model;

import com.m4ck_y.user_api_springboot.domain.dto.AddressDTO;
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

    public static AddressDTO toDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setName(address.getName());
        dto.setStreet(address.getStreet());
        dto.setCountryCode(address.getCountryCode());
        return dto;
    }

    public static Address fromDTO(AddressDTO dto) {
        Address address = new Address();
        address.setId(dto.getId());
        address.setName(dto.getName());
        address.setStreet(dto.getStreet());
        address.setCountryCode(dto.getCountryCode());
        return address;
    }
}