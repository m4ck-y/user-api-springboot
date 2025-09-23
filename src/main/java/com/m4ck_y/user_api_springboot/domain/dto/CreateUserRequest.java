package com.m4ck_y.user_api_springboot.domain.dto;

import com.m4ck_y.user_api_springboot.domain.validator.ValidPhone;
import com.m4ck_y.user_api_springboot.domain.validator.ValidTaxId;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class CreateUserRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @ValidPhone
    private String phone;

    @NotBlank
    private String password;

    @Size(min = 13, max = 13, message = "El RFC debe tener exactamente 13 caracteres")
    @ValidTaxId
    private String taxId;

    private List<AddressDTO> addresses;

}