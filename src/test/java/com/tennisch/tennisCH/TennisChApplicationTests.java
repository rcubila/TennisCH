package com.tennisch.tennisCH;

import com.tennisch.tennisCH.controller.UserController;
import com.tennisch.tennisCH.exception.GlobalExceptionHandler;
import com.tennisch.tennisCH.model.User;
import com.tennisch.tennisCH.service.UserService;
import com.tennisch.tennisCH.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TennisChApplicationTests {

	private MockMvc mockMvc;

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(userController)
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	void createUser() throws Exception {
		User user = new User(
				1L,
				"John",
				"Doe",
				"johndoe",
				"password123",
				"johndoe@example.com",
				"http://profilepic.url",
				null,
				null,
				null
		);

		when(userService.createUser(any(User.class))).thenReturn(user);

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\n" +
								"\"firstName\": \"John\",\n" +
								"\"lastName\": \"Doe\",\n" +
								"\"username\": \"johndoe\",\n" +
								"\"password\": \"password123\",\n" +
								"\"email\": \"johndoe@example.com\",\n" +
								"\"profilePictureUrl\": \"http://profilepic.url\"\n" +
								"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("User created successfully"));

		verify(userService, times(1)).createUser(any(User.class));
	}

	@Test
	void testCreateUser_UserAlreadyExistsException() throws Exception {
		// Mock the UserService to throw the exception
		when(userService.createUser(any(User.class)))
				.thenThrow(new UserAlreadyExistsException("Username already exists"));

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\n" +
								"\"firstName\": \"John\",\n" +
								"\"lastName\": \"Doe\",\n" +
								"\"username\": \"johndoe\",\n" +
								"\"password\": \"password123\",\n" +
								"\"email\": \"johndoe@example.com\",\n" +
								"\"profilePictureUrl\": \"http://profilepic.url\"\n" +
								"}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Username already exists"));

		verify(userService, times(1)).createUser(any(User.class));
	}
}
