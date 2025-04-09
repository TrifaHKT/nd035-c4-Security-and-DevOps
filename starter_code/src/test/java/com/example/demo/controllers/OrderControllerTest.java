package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

public class OrderControllerTest {
  
    private UserController userController;

    private CartController cartController;

    private OrderController orderController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    private OrderRepository orderRepo = mock(OrderRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        cartController = new CartController();
        orderController = new OrderController();

        when(itemRepo.findById(1L)).thenReturn(Optional.of(TestUtils.createItemWithStaticValue(1L)));

        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);    
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void givenUserCartWithThreeItems_whenSubmit_thenOrderSubmit() {
        when(userRepo.findByUsername("testUser1")).thenReturn(TestUtils.createUserWithStaticValue(0L, "testUser1", "abcd@4567"));

        ResponseEntity<UserOrder> response = orderController.submit("testUser1");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserOrder order = response.getBody();
        assertNotNull(order);

        assertEquals("testUser1", order.getUser().getUsername());
        assertEquals(3, order.getItems().size());
    }

    @Test
    public void giveNullUser_whenSubmit_thenReturnNotFound() {
        when(userRepo.findByUsername("testUser1")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("testUser1");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void giveNullUser_whenCheckHistory_thenReturnNotFound() {
        when(userRepo.findByUsername("testUser1")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser1");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
