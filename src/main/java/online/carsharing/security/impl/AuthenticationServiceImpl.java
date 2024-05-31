package online.carsharing.security.impl;

import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.user.UserLoginRequestDto;
import online.carsharing.dto.request.user.UserRegisterRequestDto;
import online.carsharing.dto.response.user.UserLoginResponseDto;
import online.carsharing.dto.response.user.UserResponseDto;
import online.carsharing.entity.Role;
import online.carsharing.entity.User;
import online.carsharing.exception.InvalidInputDataException;
import online.carsharing.exception.UserAlreadyExistsException;
import online.carsharing.mapper.UserMapper;
import online.carsharing.repository.UserRepository;
import online.carsharing.security.AuthenticationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegisterRequestDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new UserAlreadyExistsException("User with this email "
                    + registerDto.getEmail() + " already exists");
        }
        User user = userMapper.toUser(registerDto);
        user.setRole(Role.CUSTOMER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto loginDto) {
        User userFromDb = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(()
                        -> new InvalidInputDataException("Email or password is incorrect"));

        if (!passwordEncoder.matches(loginDto.getPassword(), userFromDb.getPassword())) {
            throw new InvalidInputDataException("Email or password is incorrect");
        }

        return new UserLoginResponseDto();
    }
}
