package online.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.user.UserLoginRequestDto;
import online.carsharing.dto.request.user.UserRegisterRequestDto;
import online.carsharing.dto.response.user.UserLoginResponseDto;
import online.carsharing.dto.response.user.UserResponseDto;
import online.carsharing.security.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User authentication", description = "Endpoints for authenticate users")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "User Registration",
            description = "This endpoint allows for the registration of a "
                    + "new user by providing the necessary"
                    + " details such as username, password, and email."
    )
    @PostMapping("/register")
    public UserResponseDto registerUser(@Valid @RequestBody UserRegisterRequestDto registerDto) {
        return authenticationService.register(registerDto);
    }

    @Operation(
            summary = "User Login",
            description = "This endpoint allows an existing user to log in"
                    + " by providing the necessary "
                    + "details such as email and password."
    )
    @PostMapping("/login")
    public UserLoginResponseDto loginUser(@Valid @RequestBody UserLoginRequestDto loginDto) {
        return authenticationService.login(loginDto);
    }
}
