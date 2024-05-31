package online.carsharing.security.impl;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.user.UserLoginRequestDto;
import online.carsharing.dto.request.user.UserRegisterRequestDto;
import online.carsharing.dto.response.user.UserLoginResponseDto;
import online.carsharing.dto.response.user.UserResponseDto;
import online.carsharing.entity.Role;
import online.carsharing.entity.RoleName;
import online.carsharing.entity.User;
import online.carsharing.exception.UserAlreadyExistsException;
import online.carsharing.mapper.UserMapper;
import online.carsharing.repository.user.RoleRepository;
import online.carsharing.repository.user.UserRepository;
import online.carsharing.security.AuthenticationService;
import online.carsharing.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponseDto register(UserRegisterRequestDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new UserAlreadyExistsException("User with this email "
                    + registerDto.getEmail() + " already exists");
        }
        User user = userMapper.toUser(registerDto);
        Role role = roleRepository.findByName(RoleName.CUSTOMER).orElseThrow(() ->
                new NoSuchElementException("Role CUSTOMER not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword())

        );

        String token = jwtUtil.generateToken(authentication.getName());
        return new UserLoginResponseDto(token);
    }
}
