package com.m4ck_y.user_api_springboot.application.service;

import com.m4ck_y.user_api_springboot.domain.dto.*;
import com.m4ck_y.user_api_springboot.domain.model.Address;
import com.m4ck_y.user_api_springboot.domain.model.User;
import com.m4ck_y.user_api_springboot.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() throws Exception {

        var aesKeyField = UserServiceImpl.class.getDeclaredField("aesKey");
        aesKeyField.setAccessible(true);
        aesKeyField.set(userService, "MySecretKey12345");
        
        testUserId = UUID.randomUUID();
        Address address1 = new Address(UUID.randomUUID(), "work", "street 1", "UK");
        Address address2 = new Address(UUID.randomUUID(), "home", "street 2", "AU");

        String encryptedPassword = userService.encryptPassword("password");

        testUser = new User(testUserId, "test@example.com", "testuser", "+1234567890",
                encryptedPassword, "XAXX010101000", "01-01-2024 00:00:00",
                Arrays.asList(address1, address2));
    }

    @Test
    void getUsers_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        List<UserDTO> result = userService.getUsers(null, null);

        assertEquals(1, result.size());
        assertEquals(testUserId, result.get(0).getId());
        verify(userRepository).findAll();
    }

    @Test
    void getUsers_withSorting_shouldReturnSortedUsers() {
        User user2 = new User(UUID.randomUUID(), "a@example.com", "auser", "+1234567890",
                "encryptedpass", "XAXX010101000", "01-01-2024 00:00:00", Arrays.asList());

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<UserDTO> result = userService.getUsers("email", null);

        assertEquals(2, result.size());
        assertEquals("a@example.com", result.get(0).getEmail());
        assertEquals("test@example.com", result.get(1).getEmail());
    }

    @Test
    void getUsers_withFiltering_shouldReturnFilteredUsers() {
        User user2 = new User(UUID.randomUUID(), "other@example.com", "otheruser", "+1234567890",
                "encryptedpass", "XAXX010101000", "01-01-2024 00:00:00", Arrays.asList());

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<UserDTO> result = userService.getUsers(null, "name+co+test");

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getName());
    }

    @Test
    void createUser_shouldCreateAndReturnUser() {
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

        when(userRepository.existsByTaxId(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO result = userService.createUser(request);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_withExistingTaxId_shouldThrowException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setTaxId("XAXX010101000");

        when(userRepository.existsByTaxId("XAXX010101000")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));

        assertEquals("Tax ID already exists", exception.getMessage());
    }

    @Test
    void updateUser_shouldUpdateAndReturnUser() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("updatedname");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO result = userService.updateUser(testUserId, request);

        assertNotNull(result);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_withNonExistingId_shouldThrowException() {
        UpdateUserRequest request = new UpdateUserRequest();

        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(testUserId, request));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        userService.deleteUser(testUserId);

        verify(userRepository).deleteById(testUserId);
    }

    @Test
    void deleteUser_withNonExistingId_shouldThrowException() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(testUserId));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void authenticate_withValidCredentials_shouldReturnTrue() {
        when(userRepository.findByTaxId("XAXX010101000")).thenReturn(Optional.of(testUser));

        boolean result = userService.authenticate("XAXX010101000", "password");

        assertTrue(result);
    }

    @Test
    void authenticate_withInvalidTaxId_shouldReturnFalse() {
        when(userRepository.findByTaxId("invalid")).thenReturn(Optional.empty());

        boolean result = userService.authenticate("invalid", "password");

        assertFalse(result);
    }

    @Test
    void authenticate_withInvalidPassword_shouldReturnFalse() {
        when(userRepository.findByTaxId("XAXX010101000")).thenReturn(Optional.of(testUser));

        boolean result = userService.authenticate("XAXX010101000", "wrongpassword");

        assertFalse(result);
    }
}