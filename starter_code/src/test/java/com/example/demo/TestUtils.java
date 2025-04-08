package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject) {
        boolean wasPrivate=false;
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            if(!f.canAccess(target)) {
                f.setAccessible(true);
                wasPrivate=true;
            }
            f.set(target,toInject);
            if(wasPrivate){
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static User createUserWithRequest(UserController userController, PasswordEncoder encoder, String username, String rawPassword, String encodedPassword) {

        when(encoder.encode(rawPassword)).thenReturn(encodedPassword);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(rawPassword);
        request.setConfirmPassword(rawPassword);

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User user = response.getBody();
        assertNotNull(user);

        return user;
    }

    public static User createUserWithStaticValue(Long id, String username, String password) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);

        Cart cart = createCartWithItems(id, user);
        user.setCart(cart);
        return user;
    }

    public static User createUserWithEmptyCart(Long id, String username, String password) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);

        Cart cart = createCartWithEmptyItem(id, user);
        user.setCart(cart);
        return user;
    }

    public static Item createItemWithStaticValue(Long id) {
        Item item = new Item();
        item.setId(id);
        item.setPrice(BigDecimal.valueOf(id * 3.1415));
        item.setName(" Mock Item " + item.getId());
        item.setDescription(" Mock Description ");
        return item;
    }

    public static ArrayList<Item> createItemsWithStaticValue() {
        ArrayList<Item> items = new ArrayList<>();
        Item item1 = createItemWithStaticValue(1L);
        Item item2 = createItemWithStaticValue(2L);
        Item item3 = createItemWithStaticValue(3L);
        items.add(item1);
        items.add(item2);
        items.add(item3);
        return items;
    }

    public static Cart createCartWithEmptyItem(Long id, User user) {
        Cart cart = new Cart();
        cart.setId(id);
        List<Item> items = new ArrayList<>();
        cart.setItems(items);
        cart.setTotal(BigDecimal.ZERO);
        cart.setUser(user);

        return cart;
    }

    public static Cart createCartWithItems(Long id, User user) {
        Cart cart = new Cart();
        cart.setId(id);
        List<Item> items = createItemsWithStaticValue();
        cart.setItems(items);
        cart.setTotal(items.stream().map(item -> item.getPrice()).reduce(BigDecimal::add).get());
        cart.setUser(user);

        return cart;
    }

}
