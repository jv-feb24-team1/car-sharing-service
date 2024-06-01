package online.carsharing.service;

import online.carsharing.dto.request.role.RoleChangeRequestDto;
import online.carsharing.dto.request.user.UserRegisterRequestDto;
import online.carsharing.dto.response.role.RoleChangeResponseDto;
import online.carsharing.dto.response.user.UserResponseDto;

public interface UserService {

    RoleChangeResponseDto updateUserRole(Long userId, RoleChangeRequestDto requestDto);

    UserResponseDto getCurrentUserProfile(Long userId);

    UserResponseDto updateCurrentUserProfile(Long userId, UserRegisterRequestDto requestDto);
}
