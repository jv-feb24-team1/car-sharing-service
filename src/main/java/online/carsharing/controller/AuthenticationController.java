package online.carsharing.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.user.UserRegisterRequestDto;
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

    @PostMapping("/register")
    public UserResponseDto registerUser(@Valid @RequestBody UserRegisterRequestDto registerDto) {
        return authenticationService.register(registerDto);
    }
}
