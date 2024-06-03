package online.carsharing.service.impl;

import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.role.RoleChangeRequestDto;
import online.carsharing.dto.request.user.UserUpdateRequestDto;
import online.carsharing.dto.response.role.RoleChangeResponseDto;
import online.carsharing.dto.response.user.UserResponseDto;
import online.carsharing.entity.Role;
import online.carsharing.entity.User;
import online.carsharing.exception.EntityNotFoundException;
import online.carsharing.exception.UserAlreadyExistsException;
import online.carsharing.mapper.RoleMapper;
import online.carsharing.mapper.UserMapper;
import online.carsharing.repository.user.RoleRepository;
import online.carsharing.repository.user.UserRepository;
import online.carsharing.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String ROLE_UPDATE_MESSAGE = "Role has been changed successful";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    @Override
    public RoleChangeResponseDto updateUserRole(Long userId, RoleChangeRequestDto requestDto) {
        User userFromDb = findUserById(userId);
        Role role = getRoleFromRequest(requestDto);

        userFromDb.getRoles().clear();
        userFromDb.getRoles().add(role);

        userRepository.save(userFromDb);

        return new RoleChangeResponseDto(ROLE_UPDATE_MESSAGE);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto getCurrentUserProfile(Long userId) {
        return userMapper.toDto(findUserById(userId));
    }

    @Override
    public UserResponseDto updateCurrentUserProfile(
            Long userId,
            UserUpdateRequestDto requestDto
    ) {
        User user = findUserById(userId);

        if (requestDto.getEmail() != null) {
            if (userRepository.existsByEmail(requestDto.getEmail())) {
                throw new UserAlreadyExistsException("User with this email "
                        + requestDto.getEmail() + " already exists");
            }
            user.setEmail(requestDto.getEmail());
        }
        if (requestDto.getFirstName() != null) {
            user.setFirstName(requestDto.getFirstName());
        }
        if (requestDto.getLastName() != null) {
            user.setLastName(requestDto.getLastName());
        }
        if (requestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }
        return userMapper.toDto(userRepository.save(user));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id " + userId + " not found"));
    }

    private Role getRoleFromRequest(RoleChangeRequestDto requestDto) {
        return roleRepository.findByType(roleMapper.toRole(requestDto))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can not find role " + requestDto.getRole() + " in DB"));
    }
}
