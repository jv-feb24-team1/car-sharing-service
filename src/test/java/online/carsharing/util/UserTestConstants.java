package online.carsharing.util;

public class UserTestConstants {
    public static final String REGISTER_PATH = "/register";
    public static final String LOGIN_PATH = "/login";
    public static final String USERS_URI = "/users";
    public static final String DB_SCRIPT_PATH = "db/script/user_db.sql";
    public static final String ROLE_PATH = "/role";
    public static final String PATH_SEPARATOR = "/";
    public static final String ME_PATH = "/me";

    public static final String CUSTOMER_EMAIL = "customer@example.com";
    public static final String CUSTOMER_PASSWORD = "password123";
    public static final String CUSTOMER_FIRST_NAME = "Customer";
    public static final String CUSTOMER_LAST_NAME = "User";
    public static final String CUSTOMER_USERNAME = "customer";

    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_CHANGED_SUCCESS = "Role has been changed successful";

    public static final Long USER_ID = 1L;
    public static final String USER_EMAIL = "john.doe@example.com";
    public static final String USER_FIRST_NAME = "John";
    public static final String USER_LAST_NAME = "Doe";
    public static final String USER_PASSWORD = "newPassword";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String ORIGINAL_EMAIL = "original.email@example.com";
    public static final String ORIGINAL_FIRST_NAME = "Original";
    public static final String ORIGINAL_LAST_NAME = "Name";
    public static final String OLD_PASSWORD = "oldPassword";
    public static final String EXISTING_EMAIL = "existing@example.com";
    public static final String INVALID_ROLE = "INVALID_ROLE";
    public static final String ADMIN_USERNAME = "admin";

    public static final String TOKEN_REGEX = "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$";

    public static final String DB_PATH = "classpath:db/script/user_db.sql";
}
