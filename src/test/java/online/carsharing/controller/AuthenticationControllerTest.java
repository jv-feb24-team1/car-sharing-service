package online.carsharing.controller;

import static online.carsharing.util.UserTestConstants.CUSTOMER_EMAIL;
import static online.carsharing.util.UserTestConstants.CUSTOMER_PASSWORD;
import static online.carsharing.util.UserTestConstants.DB_PATH;
import static online.carsharing.util.UserTestConstants.LOGIN_PATH;
import static online.carsharing.util.UserTestConstants.REGISTER_PATH;
import static online.carsharing.util.UserTestConstants.TOKEN_REGEX;
import static online.carsharing.util.UserTestConstants.USER_EMAIL;
import static online.carsharing.util.UserTestConstants.USER_FIRST_NAME;
import static online.carsharing.util.UserTestConstants.USER_LAST_NAME;
import static online.carsharing.util.UserTestConstants.USER_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.carsharing.dto.request.user.UserLoginRequestDto;
import online.carsharing.dto.request.user.UserRegisterRequestDto;
import online.carsharing.dto.response.user.UserLoginResponseDto;
import online.carsharing.dto.response.user.UserResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    @DisplayName("Register a new user and verify the response")
    void registerNewUser_RegisterUser_ReturnsUserResponse() throws Exception {

        UserRegisterRequestDto userRequest = new UserRegisterRequestDto();
        userRequest.setFirstName(
                USER_FIRST_NAME);
        userRequest.setLastName(
                USER_LAST_NAME);
        userRequest.setEmail(
                USER_EMAIL);
        userRequest.setPassword(
                USER_PASSWORD);
        userRequest.setConfirmPassword(
                USER_PASSWORD);

        String requestJson = objectMapper.writeValueAsString(userRequest);

        MvcResult mvcResult = mockMvc.perform(post(
                        REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        UserResponseDto responseDto = objectMapper.readValue(responseJson, UserResponseDto.class);

        assertAll(
                () -> assertNotNull(responseDto),
                () -> assertEquals(
                        USER_FIRST_NAME, responseDto.getFirstName()),
                () -> assertEquals(
                        USER_LAST_NAME, responseDto.getLastName()),
                () -> assertEquals(
                        USER_EMAIL, responseDto.getEmail())
        );
    }

    @Test
    @DisplayName("Login with valid credentials and verify the token")
    @Sql(scripts = DB_PATH, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void loginWithValidCredentials_LoginUser_ReturnsValidToken() throws Exception {

        UserLoginRequestDto loginRequest = new UserLoginRequestDto();
        loginRequest.setEmail(
                CUSTOMER_EMAIL);
        loginRequest.setPassword(
                CUSTOMER_PASSWORD);

        String requestJson = objectMapper.writeValueAsString(loginRequest);

        MvcResult mvcResult = mockMvc.perform(post(
                        LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        UserLoginResponseDto responseDto = objectMapper.readValue(responseJson,
                UserLoginResponseDto.class);

        assertNotNull(responseDto);
        assertTrue(responseDto.getToken().matches(
                TOKEN_REGEX));
    }
}
