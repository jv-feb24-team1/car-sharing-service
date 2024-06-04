package online.carsharing.controller;

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
import online.carsharing.util.CarTestConstants;
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
        addThreeCars(dataSource);
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
        modelX.setId(CarTestConstants.DEFAULT_ID);
        modelX.setModel(CarTestConstants.CAR_MODEL);
        modelX.setBrand(CarTestConstants.CAR_BRAND);
        modelX.setType(Type.SEDAN);
        modelX.setDailyFee(CarTestConstants.DEFAULT_DAILY_FEE);

        CarResponseDto modelY = new CarResponseDto();
        modelY.setId(CarTestConstants.SECOND_CAR_ID);
        modelY.setModel(CarTestConstants.SECOND_CAR_MODEL);
        modelY.setBrand(CarTestConstants.SECOND_CAR_BRAND);
        modelY.setType(Type.SUV);
        modelY.setDailyFee(CarTestConstants.SECOND_CAR_DAILY_FEE);

        List<CarResponseDto> expected = new ArrayList<>();
        expected.add(modelX);
        expected.add(modelY);

        MvcResult result = mockMvc.perform(get(CarTestConstants.CARS_URL)
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
        expected.setId(CarTestConstants.DEFAULT_ID);
        expected.setModel(CarTestConstants.CAR_MODEL);
        expected.setBrand(CarTestConstants.CAR_BRAND);
        expected.setType(Type.SEDAN);
        expected.setDailyFee(CarTestConstants.DEFAULT_DAILY_FEE);

        MvcResult result = mockMvc.perform(get(CarTestConstants.CARS_URL + CarTestConstants.URL_ID)
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
    @WithMockUser(roles = {CarTestConstants.ADMIN})
    @DisplayName("Create a new car with valid data")
    public void createCar_GivenValidData_ReturnsCar() throws Exception {
        CreateCarRequestDto requestDto = new CreateCarRequestDto();
        requestDto.setModel(CarTestConstants.CAR_MODEL);
        requestDto.setBrand(CarTestConstants.CAR_BRAND);
        requestDto.setType(Type.SEDAN);
        requestDto.setInventory(CarTestConstants.DEFAULT_INVENTORY);
        requestDto.setDailyFee(CarTestConstants.DEFAULT_DAILY_FEE);

        MvcResult result = mockMvc.perform(post(CarTestConstants.CARS_URL)
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
    @WithMockUser(roles = {CarTestConstants.ADMIN})
    @DisplayName("Update existing car's inventory")
    public void updateCarById_GivenValidData_ReturnsCar() throws Exception {
        CarUpdateDto updateDto = new CarUpdateDto();
        updateDto.setInventory(CarTestConstants.DEFAULT_INVENTORY);

        CarResponseDto expected = new CarResponseDto();
        expected.setId(CarTestConstants.DEFAULT_ID);
        expected.setModel(CarTestConstants.CAR_MODEL);
        expected.setBrand(CarTestConstants.CAR_BRAND);
        expected.setType(Type.SEDAN);
        expected.setDailyFee(CarTestConstants.DEFAULT_DAILY_FEE);

        MvcResult result = mockMvc.perform(put(CarTestConstants.CARS_URL + CarTestConstants.URL_ID)
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
    @WithMockUser(roles = {CarTestConstants.ADMIN})
    @DisplayName("Delete existing car")
    public void deleteCarById_GivenValidId_NoContentStatus() throws Exception {
        mockMvc.perform(delete(CarTestConstants.CARS_URL + CarTestConstants.URL_ID)
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
                    new ClassPathResource(CarTestConstants.CLEAN_CARS_SCRIPT_PATH));
        }
    }

    @SneakyThrows
    private void addThreeCars(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(CarTestConstants.SETUP_CARS_SCRIPT_PATH));
        }
    }
}
