package online.carsharing.controller;

import static online.carsharing.util.CarTestConstants.CARS_URL;
import static online.carsharing.util.CarTestConstants.CAR_BRAND;
import static online.carsharing.util.CarTestConstants.CAR_MODEL;
import static online.carsharing.util.CarTestConstants.CLEAN_CARS_SCRIPT_PATH;
import static online.carsharing.util.CarTestConstants.DEFAULT_DAILY_FEE;
import static online.carsharing.util.CarTestConstants.DEFAULT_ID;
import static online.carsharing.util.CarTestConstants.DEFAULT_INVENTORY;
import static online.carsharing.util.CarTestConstants.MANAGER;
import static online.carsharing.util.CarTestConstants.SECOND_CAR_BRAND;
import static online.carsharing.util.CarTestConstants.SECOND_CAR_DAILY_FEE;
import static online.carsharing.util.CarTestConstants.SECOND_CAR_ID;
import static online.carsharing.util.CarTestConstants.SECOND_CAR_MODEL;
import static online.carsharing.util.CarTestConstants.SETUP_CARS_SCRIPT_PATH;
import static online.carsharing.util.CarTestConstants.URL_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import online.carsharing.dto.request.car.CreateCarRequestDto;
import online.carsharing.dto.response.car.CarResponseDto;
import online.carsharing.dto.update.car.CarUpdateDto;
import online.carsharing.entity.Type;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext context,
            @Autowired DataSource dataSource) {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) {
        addCars(dataSource);
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Test
    @WithMockUser
    @DisplayName("Get a list of all existing cars")
    public void getAll_GivenCars_ReturnsAllCars() throws Exception {
        CarResponseDto modelX = new CarResponseDto();
        modelX.setId(DEFAULT_ID);
        modelX.setModel(CAR_MODEL);
        modelX.setBrand(CAR_BRAND);
        modelX.setType(Type.SEDAN);
        modelX.setDailyFee(DEFAULT_DAILY_FEE);

        CarResponseDto modelY = new CarResponseDto();
        modelY.setId(SECOND_CAR_ID);
        modelY.setModel(SECOND_CAR_MODEL);
        modelY.setBrand(SECOND_CAR_BRAND);
        modelY.setType(Type.SUV);
        modelY.setDailyFee(SECOND_CAR_DAILY_FEE);

        List<CarResponseDto> expected = new ArrayList<>();
        expected.add(modelX);
        expected.add(modelY);

        MvcResult result = mockMvc.perform(get(CARS_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CarResponseDto[].class);

        assertNotNull(actual);
        assertEquals(expected, Arrays.asList(actual));
    }

    @Test
    @WithMockUser
    @DisplayName("Get a car by valid ID")
    public void getCarById_GivenId_ReturnsCar() throws Exception {
        CarResponseDto expected = new CarResponseDto();
        expected.setId(DEFAULT_ID);
        expected.setModel(CAR_MODEL);
        expected.setBrand(CAR_BRAND);
        expected.setType(Type.SEDAN);
        expected.setDailyFee(DEFAULT_DAILY_FEE);

        MvcResult result = mockMvc.perform(get(CARS_URL + URL_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CarResponseDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(roles = {MANAGER})
    @DisplayName("Create a new car with valid data")
    public void createCar_GivenValidData_ReturnsCar(@Autowired DataSource dataSource)
            throws Exception {
        tearDown(dataSource);
        CreateCarRequestDto requestDto = new CreateCarRequestDto();
        requestDto.setModel(CAR_MODEL);
        requestDto.setBrand(CAR_BRAND);
        requestDto.setType(Type.SEDAN);
        requestDto.setInventory(DEFAULT_INVENTORY);
        requestDto.setDailyFee(DEFAULT_DAILY_FEE);

        MvcResult result = mockMvc.perform(post(CARS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CarResponseDto.class);

        assertNotNull(actual);
        assertEquals(requestDto.getModel(), actual.getModel());
        assertEquals(requestDto.getBrand(), actual.getBrand());
        assertEquals(requestDto.getType(), actual.getType());
        assertEquals(requestDto.getDailyFee(), actual.getDailyFee());
    }

    @Test
    @WithMockUser(roles = {MANAGER})
    @DisplayName("Update existing car's inventory")
    public void updateCarById_GivenValidData_ReturnsCar() throws Exception {
        CarUpdateDto updateDto = new CarUpdateDto();
        updateDto.setInventory(DEFAULT_INVENTORY);

        CarResponseDto expected = new CarResponseDto();
        expected.setId(DEFAULT_ID);
        expected.setModel(CAR_MODEL);
        expected.setBrand(CAR_BRAND);
        expected.setType(Type.SEDAN);
        expected.setDailyFee(DEFAULT_DAILY_FEE);

        MvcResult result = mockMvc.perform(put(CARS_URL + URL_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CarResponseDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(roles = {MANAGER})
    @DisplayName("Delete existing car")
    public void deleteCarById_GivenValidId_NoContentStatus() throws Exception {
        mockMvc.perform(delete(CARS_URL + URL_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(CLEAN_CARS_SCRIPT_PATH));
        }
    }

    @SneakyThrows
    private void addCars(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(SETUP_CARS_SCRIPT_PATH));
        }
    }
}
