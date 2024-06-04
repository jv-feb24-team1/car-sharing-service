package online.carsharing.controller;

import static online.carsharing.util.UserTestConstants.ADMIN_USERNAME;
import static online.carsharing.util.UserTestConstants.CUSTOMER_EMAIL;
import static online.carsharing.util.UserTestConstants.CUSTOMER_FIRST_NAME;
import static online.carsharing.util.UserTestConstants.CUSTOMER_LAST_NAME;
import static online.carsharing.util.UserTestConstants.CUSTOMER_PASSWORD;
import static online.carsharing.util.UserTestConstants.CUSTOMER_USERNAME;
import static online.carsharing.util.UserTestConstants.DB_SCRIPT_PATH;
import static online.carsharing.util.UserTestConstants.ME_PATH;
import static online.carsharing.util.UserTestConstants.PATH_SEPARATOR;
import static online.carsharing.util.UserTestConstants.ROLE_CHANGED_SUCCESS;
import static online.carsharing.util.UserTestConstants.ROLE_CUSTOMER;
import static online.carsharing.util.UserTestConstants.ROLE_MANAGER;
import static online.carsharing.util.UserTestConstants.ROLE_PATH;
import static online.carsharing.util.UserTestConstants.USERS_URI;
import static online.carsharing.util.UserTestConstants.USER_EMAIL;
import static online.carsharing.util.UserTestConstants.USER_FIRST_NAME;
import static online.carsharing.util.UserTestConstants.USER_ID;
import static online.carsharing.util.UserTestConstants.USER_LAST_NAME;
import static online.carsharing.util.UserTestConstants.USER_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import online.carsharing.dto.request.role.RoleChangeRequestDto;
import online.carsharing.dto.request.user.UserUpdateRequestDto;
import online.carsharing.dto.response.role.RoleChangeResponseDto;
import online.carsharing.dto.response.user.UserResponseDto;
import online.carsharing.entity.User;
import online.carsharing.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void beforeEach(@Autowired DataSource dataSource) throws SQLException {
        executeSqlScript(dataSource, DB_SCRIPT_PATH);
    }

    @Test
    @DisplayName("Update User Role")
    @WithMockUser(username = ADMIN_USERNAME, roles = {ROLE_MANAGER})
    void updateUserRole_UpdateUserRole_ReturnsExpectedResponse() throws Exception {

        RoleChangeRequestDto requestDto = createRoleChangeRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        String url = USERS_URI + PATH_SEPARATOR + USER_ID + ROLE_PATH;

        MvcResult result = mockMvc.perform(put(url)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RoleChangeResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        RoleChangeResponseDto.class);
        assertNotNull(actual);
        assertTrue(actual.getMessage().contains(ROLE_CHANGED_SUCCESS));
    }

    @Test
    @WithMockUser(username = CUSTOMER_USERNAME, roles = {ROLE_CUSTOMER})
    @DisplayName("Get User Profile Info")
    void getUserProfile_GetCurrentUserProfile_ReturnsExpectedProfile() throws Exception {

        UserResponseDto expected = new UserResponseDto();
        expected.setId(USER_ID);
        expected.setFirstName(CUSTOMER_FIRST_NAME);
        expected.setLastName(CUSTOMER_LAST_NAME);
        expected.setEmail(CUSTOMER_EMAIL);

        mockUserAuthentication(expected);

        when(userService.getCurrentUserProfile(anyLong())).thenReturn(expected);

        MvcResult result = mockMvc.perform(get(USERS_URI + ME_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = CUSTOMER_USERNAME, roles = {ROLE_CUSTOMER})
    @DisplayName("Update User Profile Info")
    void updateUserProfile_UpdateCurrentUserProfile_ReturnsUpdatedProfile() throws Exception {

        UserUpdateRequestDto requestDto = createUserUpdateRequestDto();
        UserResponseDto expected = createUserResponseDto();

        mockUserAuthentication(expected);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        String url = USERS_URI + ME_PATH;

        when(userService.updateCurrentUserProfile(anyLong(), any(
                UserUpdateRequestDto.class)))
                .thenReturn(expected);

        MvcResult result = mockMvc.perform(patch(url)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);
        assertNotNull(actual);
        assertTrue(actual.equals(expected));
    }

    private void executeSqlScript(DataSource dataSource, String scriptPath) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(scriptPath));
        }
    }

    private RoleChangeRequestDto createRoleChangeRequestDto() {
        RoleChangeRequestDto requestDto = new RoleChangeRequestDto();
        requestDto.setRole(ROLE_MANAGER);
        return requestDto;
    }

    private UserResponseDto createUserResponseDto() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(USER_ID);
        userResponseDto.setFirstName(USER_FIRST_NAME);
        userResponseDto.setLastName(USER_LAST_NAME);
        userResponseDto.setEmail(USER_EMAIL);
        return userResponseDto;
    }

    private UserUpdateRequestDto createUserUpdateRequestDto() {
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();
        requestDto.setFirstName(USER_FIRST_NAME);
        requestDto.setLastName(USER_LAST_NAME);
        requestDto.setEmail(USER_EMAIL);
        requestDto.setPassword(USER_PASSWORD);
        requestDto.setConfirmPassword(USER_PASSWORD);
        return requestDto;
    }

    private void mockUserAuthentication(UserResponseDto userResponseDto) {
        User expectedUser = new User();
        expectedUser.setId(USER_ID);
        expectedUser.setFirstName(CUSTOMER_FIRST_NAME);
        expectedUser.setLastName(CUSTOMER_LAST_NAME);
        expectedUser.setEmail(CUSTOMER_EMAIL);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                expectedUser, CUSTOMER_PASSWORD);
        SecurityContextHolder.getContext().setAuthentication(
                authenticationManager.authenticate(authenticationToken));
    }
}
