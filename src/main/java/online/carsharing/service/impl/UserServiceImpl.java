package online.carsharing.service.impl;

import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.role.RoleChangeRequestDto;
import online.carsharing.dto.request.user.UserRegisterRequestDto;
import online.carsharing.dto.response.role.RoleChangeResponseDto;
import online.carsharing.dto.response.user.UserResponseDto;
import online.carsharing.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public RoleChangeResponseDto updateUserRole(Long userId, RoleChangeRequestDto requestDto) {
        return null;
    }

    @Override
    public UserResponseDto getCurrentUserProfile(Long userId) {
        return null;
    }

    @Override
    public UserResponseDto updateCurrentUserProfile(
            Long userId,
            UserRegisterRequestDto requestDto
    ) {
        return null;
    }
}
