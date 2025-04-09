package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.requests.CreateUserRequest;

import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transactional
@SpringBootTest(classes = SareetaApplication.class)
@AutoConfigureMockMvc
public class SareetaApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(SareetaApplicationTests.class);

	private static final String TEST_USERNAME = "TestUser1";
	private static final String TEST_PWD = "abcd@4567";
	private static final String TEST_PWD_CONFIRM = "abcd@4567";

	@Autowired
	private CartController cartController;

	@Autowired
	private ItemController itemController;

	@Autowired
	private OrderController orderController;

	@Autowired
	private UserController userController;

	private CreateUserRequest getCreateUserRequest() {
		CreateUserRequest createUserRequest = new CreateUserRequest();
		createUserRequest.setUsername(TEST_USERNAME);
		createUserRequest.setPassword(TEST_PWD);
		createUserRequest.setConfirmPassword(TEST_PWD_CONFIRM);
		return createUserRequest;
	}

	@Test
	public void givenNewUser_whenRegister_thenCreateUser() throws Exception {
			CreateUserRequest createUserRequest = getCreateUserRequest();
			ResponseEntity<User> response = userController.createUser(createUserRequest);
			
			assertNotNull(response);
			assertEquals(HttpStatus.OK, response.getStatusCode());

			User responseUser = response.getBody();

			assertNotNull(responseUser);
	}

	@Test
	public void givenNewUserName_whenFindByUserName_thenReturnUser() throws Exception {
			CreateUserRequest createUserRequest = getCreateUserRequest();
			ResponseEntity<User> response = userController.createUser(createUserRequest);
			
			assertNotNull(response);
			assertEquals(HttpStatus.OK, response.getStatusCode());

			User responseUser = response.getBody();

			assertNotNull(responseUser);

			ResponseEntity<User> findResponse = userController.findByUserName(TEST_USERNAME);

			assertNotNull(findResponse);
			assertEquals(HttpStatus.OK, findResponse.getStatusCode());

			User findResponseUser = findResponse.getBody();

			assertNotNull(findResponseUser);
			assertEquals(responseUser.getUsername(), findResponseUser.getUsername());
			assertEquals(responseUser.getId(), findResponseUser.getId());
	}

	@Test
	public void givePresetItemsData_whenGetItesms_thenReturnItems() throws Exception {
			ResponseEntity<List<Item>> response = itemController.getItems();

			assertNotNull(response);
			assertEquals(HttpStatus.OK, response.getStatusCode());

			List<Item> items = response.getBody();

			assertNotNull(items);
			assertFalse(items.isEmpty());
	}

	@Test
	public void giveItemId_whenGetItemById_thenReturnItem() throws Exception {
			ResponseEntity<List<Item>> response = itemController.getItems();

			assertNotNull(response);
			assertEquals(HttpStatus.OK, response.getStatusCode());

			List<Item> items = response.getBody();

			assertNotNull(items);
			assertFalse(items.isEmpty());

			Item item = items.get(0);

			ResponseEntity<Item> findResponse = itemController.getItemById(item.getId());

			assertNotNull(findResponse);
			assertEquals(HttpStatus.OK, findResponse.getStatusCode());

			Item findResponseItem = findResponse.getBody();

			assertNotNull(findResponseItem);
			assertEquals(item.getName(), findResponseItem.getName());
	}

	@Test
	public void giveItemName_whenGetItemsByName_thenReturnItems() throws Exception {
			ResponseEntity<List<Item>> response = itemController.getItems();

			assertNotNull(response);
			assertEquals(HttpStatus.OK, response.getStatusCode());

			List<Item> items = response.getBody();

			assertNotNull(items);
			assertFalse(items.isEmpty());

			Item item = items.get(0);

			ResponseEntity<List<Item>> findResponse = itemController.getItemsByName(item.getName());

			assertNotNull(findResponse);
			assertEquals(HttpStatus.OK, findResponse.getStatusCode());

			List<Item> findResponseItems = findResponse.getBody();

			assertNotNull(findResponseItems);
			assertFalse(findResponseItems.isEmpty());
	}

	@Test void giveItemName_whenGetItemsByIncorrectName_thenReturnItems() throws Exception {
			ResponseEntity<List<Item>> response = itemController.getItems();

			assertNotNull(response);
			assertEquals(HttpStatus.OK, response.getStatusCode());

			List<Item> items = response.getBody();

			assertNotNull(items);
			assertFalse(items.isEmpty());

			Item item = items.get(0);

			ResponseEntity<List<Item>> findResponse = itemController.getItemsByName(item.getName() + "123");

			assertNotNull(findResponse);
			assertEquals(HttpStatus.NOT_FOUND, findResponse.getStatusCode());
	}

	@Test
	public void contextLoads() {

	}

}
