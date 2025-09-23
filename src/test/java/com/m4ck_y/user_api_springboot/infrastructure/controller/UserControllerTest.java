package com.m4ck_y.user_api_springboot.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m4ck_y.user_api_springboot.domain.dto.*;
import com.m4ck_y.user_api_springboot.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO testUserDTO;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        AddressDTO address1 = new AddressDTO();
        address1.setId(UUID.randomUUID());
        address1.setName("work");
        address1.setStreet("street 1");
        address1.setCountryCode("UK");

        AddressDTO address2 = new AddressDTO();
        address2.setId(UUID.randomUUID());
        address2.setName("home");
        address2.setStreet("street 2");
        address2.setCountryCode("AU");

        testUserDTO = new UserDTO();
        testUserDTO.setId(testUserId);
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setName("testuser");
        testUserDTO.setPhone("+1234567890");
        testUserDTO.setTaxId("XAXX010101000");
        testUserDTO.setCreatedAt("01-01-2024 00:00:00");
        testUserDTO.setAddresses(Arrays.asList(address1, address2));
    }

    @Test
    void getUsers_shouldReturnUsers() throws Exception {
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.getUsers(null, null)).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testUserId.toString()));
    }

    @Test
    void getUsers_withFilter_shouldReturnFilteredUsers() throws Exception {
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.getUsers(null, "name co test")).thenReturn(users);

        mockMvc.perform(get("/users?filter=name%20co%20test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.getUsers(null, null)).thenReturn(users);

        mockMvc.perform(get("/users/{id}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.toString()));
    }

    @Test
    void createUser_shouldCreateAndReturnUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("new@example.com");
        request.setName("newuser");
        request.setPhone("+1234567890");
        request.setPassword("password");
        request.setTaxId("XAXX010101000");
        AddressDTO address = new AddressDTO();
        address.setId(UUID.randomUUID());
        address.setName("work");
        address.setStreet("street");
        address.setCountryCode("US");
        request.setAddresses(Arrays.asList(address));

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(testUserDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testUserId.toString()));
    }

    @Test
    void createUser_withInvalidData_shouldReturnBadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        // Invalid request

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_shouldUpdateAndReturnUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("updated");
        request.setPhone("+1234567890");
        request.setTaxId("XAXX010101000");

        when(userService.updateUser(eq(testUserId), any(UpdateUserRequest.class))).thenReturn(testUserDTO);

        mockMvc.perform(patch("/users/{id}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.toString()));
    }

    @Test
    void deleteUser_shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", testUserId))
                .andExpect(status().isNoContent());
    }

    @Test
    void login_shouldAuthenticateAndReturnSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setTaxId("XAXX010101000");
        request.setPassword("password");

        when(userService.authenticate("XAXX010101000", "password")).thenReturn(true);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void login_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setTaxId("invalid");
        request.setPassword("wrong");

        when(userService.authenticate("invalid", "wrong")).thenReturn(false);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }
}