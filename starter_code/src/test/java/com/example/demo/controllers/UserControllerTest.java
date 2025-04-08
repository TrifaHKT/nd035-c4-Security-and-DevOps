package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepo=mock(UserRepository.class);

    private CartRepository cartRepo=mock(CartRepository.class);

    private BCryptPasswordEncoder encoder=mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void givenNewUser_whenRegister_thenCreateUser() throws Exception {
        User newUser = TestUtils.createUserWithRequest(userController, encoder, "testUser1", "abcd@4567", "4567@abcd");
        assertEquals(0, newUser.getId());
        assertEquals("testUser1", newUser.getUsername());
        assertEquals("4567@abcd", newUser.getPassword());
    }

    @Test
    public void giveUnavailableShortPasswordNewUser_whenRegister_thenCreateUserFail() throws Exception {
        when(encoder.encode("abcd")).thenReturn("4567");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUser1");
        request.setPassword("abcd");
        request.setConfirmPassword("abcd");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void giveUnmatchedPassworddNewUser_whenRegister_thenCreateUserFail() throws Exception {
        when(encoder.encode("abcd12345")).thenReturn("12345abcd");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUser1");
        request.setPassword("abcd12345");
        request.setConfirmPassword("abcd12346");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void givenNewUser_whenFetchUserByName_thenReturnNewUser() throws Exception {

        Long MOCK_ID = 1L;
        String MOCK_USERNAME = "testUser1";
        String MOCK_PASSWORD = "abcd@4567";

        User newUser = TestUtils.createUserWithStaticValue(MOCK_ID, MOCK_USERNAME, MOCK_PASSWORD);

        when(userRepo.findByUsername(MOCK_USERNAME)).thenReturn(newUser);
        ResponseEntity<User> mockResponse = userController.findByUserName(MOCK_USERNAME);

        assertNotNull(mockResponse);
        assertEquals(HttpStatus.OK, mockResponse.getStatusCode());

        User actualUser = mockResponse.getBody();

        assertNotNull(actualUser);

        assertEquals(MOCK_ID, actualUser.getId());
        assertEquals(MOCK_USERNAME, actualUser.getUsername());
        assertEquals(MOCK_PASSWORD, actualUser.getPassword());

    }

    @Test
    public void givenNewUser_whenFetchUserById_thenReturnNewUser() throws Exception {

        Long MOCK_ID = 1L;
        String MOCK_USERNAME = "testUser1";
        String MOCK_PASSWORD = "abcd@4567";

        User newUser = TestUtils.createUserWithStaticValue(MOCK_ID, MOCK_USERNAME, MOCK_PASSWORD);

        when(userRepo.findById(MOCK_ID)).thenReturn(java.util.Optional.of(newUser));
        ResponseEntity<User> mockResponse = userController.findById(MOCK_ID);

        assertNotNull(mockResponse);
        assertEquals(HttpStatus.OK, mockResponse.getStatusCode());

        User actualUser = mockResponse.getBody();

        assertNotNull(actualUser);

        assertEquals(MOCK_ID, actualUser.getId());
        assertEquals(MOCK_USERNAME, actualUser.getUsername());
        assertEquals(MOCK_PASSWORD, actualUser.getPassword());

    }
}