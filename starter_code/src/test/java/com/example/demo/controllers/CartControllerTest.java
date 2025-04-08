package com.example.demo.controllers;


import com.example.demo.model.requests.ModifyCartRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;

public class CartControllerTest {

    private UserController userController;

    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        cartController = new CartController();

        when(itemRepo.findById(1L)).thenReturn(Optional.of(TestUtils.createItemWithStaticValue(1L)));

        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);    
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void givenItemIdAndQuantiry_whenAddToCart_thenCreateCart() throws Exception {

        when(userRepo.findByUsername("testUser1")).thenReturn(TestUtils.createUserWithEmptyCart(0L, "testUser1", "abcd@4567"));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser1"); 
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Cart cart = response.getBody();
        assertNotNull(cart);

        assertEquals(0L, cart.getId());
        assertEquals(0L, cart.getUser().getId());
        assertEquals(1, cart.getItems().size());
        assertEquals(1L, cart.getItems().get(0).getId());
    }

    @Test
    public void givenUserCartWithThreeItems_whenRemoveFromCart_thenRemoveItem() throws Exception {

        when(userRepo.findByUsername("testUser1")).thenReturn(TestUtils.createUserWithStaticValue(0L, "testUser1", "abcd@4567"));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser1");
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Cart cart = response.getBody();
        assertNotNull(cart);

        assertEquals(0L, cart.getId());
        assertEquals(0L, cart.getUser().getId());
        assertEquals(2, cart.getItems().size());
    }
  
}
