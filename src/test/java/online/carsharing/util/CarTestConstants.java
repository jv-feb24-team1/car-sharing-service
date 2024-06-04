package online.carsharing.util;

import java.math.BigDecimal;

public class CarTestConstants {
    public static final String CARS_URL = "/cars";
    public static final String CAR_MODEL = "Test Model";
    public static final String SECOND_CAR_MODEL = "Second Test Model";
    public static final String CAR_BRAND = "Test Brand";
    public static final String SECOND_CAR_BRAND = "Second Test Brand";
    public static final String SETUP_CARS_SCRIPT_PATH = "db/script/setupCars.sql";
    public static final String CLEAN_CARS_SCRIPT_PATH = "db/script/cleanCars.sql";
    public static final String URL_ID = "/1";
    public static final String MANAGER = "MANAGER";
    public static final Long DEFAULT_ID = 1L;
    public static final Long SECOND_CAR_ID = 2L;
    public static final Integer DEFAULT_INVENTORY = 5;
    public static final BigDecimal DEFAULT_DAILY_FEE = BigDecimal.valueOf(100.55);
    public static final BigDecimal SECOND_CAR_DAILY_FEE = BigDecimal.valueOf(150.45);
}
