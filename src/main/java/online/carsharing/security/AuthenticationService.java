package online.carsharing.security;

import online.carsharing.dto.request.user.UserRegisterRequestDto;
import online.carsharing.dto.response.user.UserResponseDto;

public interface AuthenticationService {
    UserResponseDto register(UserRegisterRequestDto registerDto);
}
