package online.carsharing.service;

import static online.carsharing.util.UserTestConstants.ENCODED_PASSWORD;
import static online.carsharing.util.UserTestConstants.EXISTING_EMAIL;
import static online.carsharing.util.UserTestConstants.INVALID_ROLE;
import static online.carsharing.util.UserTestConstants.OLD_PASSWORD;
import static online.carsharing.util.UserTestConstants.ORIGINAL_EMAIL;
import static online.carsharing.util.UserTestConstants.ORIGINAL_FIRST_NAME;
import static online.carsharing.util.UserTestConstants.ORIGINAL_LAST_NAME;
import static online.carsharing.util.UserTestConstants.ROLE_CHANGED_SUCCESS;
import static online.carsharing.util.UserTestConstants.ROLE_MANAGER;
import static online.carsharing.util.UserTestConstants.USER_EMAIL;
import static online.carsharing.util.UserTestConstants.USER_FIRST_NAME;
import static online.carsharing.util.UserTestConstants.USER_ID;
import static online.carsharing.util.UserTestConstants.USER_LAST_NAME;
import static online.carsharing.util.UserTestConstants.USER_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import online.carsharing.dto.request.role.RoleChangeRequestDto;
import online.carsharing.dto.request.user.UserUpdateRequestDto;
import online.carsharing.dto.response.role.RoleChangeResponseDto;
import online.carsharing.dto.response.user.UserResponseDto;
import online.carsharing.entity.Role;
import online.carsharing.entity.RoleType;
import online.carsharing.entity.User;
import online.carsharing.exception.EntityNotFoundException;
import online.carsharing.exception.UserAlreadyExistsException;
import online.carsharing.mapper.RoleMapper;
import online.carsharing.mapper.UserMapper;
import online.carsharing.repository.user.RoleRepository;
import online.carsharing.repository.user.UserRepository;
import online.carsharing.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Update user role and return the result")
    void updateUserRole_ValidUser_ShouldReturnUpdatedRoleMessage() {
        Long userId = USER_ID;
        RoleChangeRequestDto requestDto = createRoleChangeRequestDtoWithManagerRole();

        User user = createUserWithIdAndRoles();

        Role role = createRoleWithType();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByType(any())).thenReturn(Optional.of(role));

        RoleChangeResponseDto response = userService.updateUserRole(userId, requestDto);

        assertEquals(ROLE_CHANGED_SUCCESS, response.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Get user profile")
    void getCurrentUserProfile_ValidUser_ShouldReturnUserProfile() {
        Long userId = USER_ID;
        User user = createUserWithId();
        UserResponseDto userResponseDto = createUserResponseDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getCurrentUserProfile(userId);

        assertEquals(userId, result.getId());
    }

    @Test
    @DisplayName("Update user profile and return updated profile")
    void updateCurrentUserProfile_ValidUser_ShouldReturnUpdatedProfile() {
        Long userId = USER_ID;
        UserUpdateRequestDto requestDto = createUserUpdateRequestDto();

        User user = createUserWithDetails(
                userId,
                ORIGINAL_EMAIL,
                ORIGINAL_FIRST_NAME,
                ORIGINAL_LAST_NAME,
                OLD_PASSWORD,
                Set.of(new Role())
        );

        User updatedUser = createUserWithDetails(
                userId,
                requestDto.getEmail(),
                requestDto.getFirstName(),
                requestDto.getLastName(),
                ENCODED_PASSWORD,
                Set.of(new Role())
        );

        UserResponseDto userResponseDto = createUserResponseDto(
                userId,
                requestDto.getEmail(),
                requestDto.getFirstName(),
                requestDto.getLastName()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword()))
                .thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(any(User.class))).thenReturn(userResponseDto);

        UserResponseDto result = userService.updateCurrentUserProfile(userId, requestDto);

        assertEquals(userId, result.getId());
        assertEquals(requestDto.getEmail(), result.getEmail());
        assertEquals(requestDto.getFirstName(), result.getFirstName());
        assertEquals(requestDto.getLastName(), result.getLastName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Update user profile with existing email should throw exception")
    void updateCurrentUserProfile_ExistingEmail_ShouldThrowException() {
        Long userId = USER_ID;
        UserUpdateRequestDto requestDto = createUserUpdateRequestDtoWithExistingEmail();

        User user = createUserWithId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.updateCurrentUserProfile(userId, requestDto));
    }

    @Test
    @DisplayName("Get user profile with invalid user ID should throw exception")
    void getCurrentUserProfile_InvalidUserId_ShouldThrowException() {
        Long userId = USER_ID;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.getCurrentUserProfile(userId));
    }

    @Test
    @DisplayName("Update user role with invalid role should throw exception")
    void updateUserRole_InvalidRole_ShouldThrowException() {
        Long userId = USER_ID;
        RoleChangeRequestDto requestDto = createRoleChangeRequestDtoWithInvalidRole();

        User user = createUserWithId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByType(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.updateUserRole(userId, requestDto));
    }

    private RoleChangeRequestDto createRoleChangeRequestDtoWithInvalidRole() {
        RoleChangeRequestDto requestDto = new RoleChangeRequestDto();
        requestDto.setRole(INVALID_ROLE);
        return requestDto;
    }

    private RoleChangeRequestDto createRoleChangeRequestDtoWithManagerRole() {
        RoleChangeRequestDto requestDto = new RoleChangeRequestDto();
        requestDto.setRole(ROLE_MANAGER);
        return requestDto;
    }

    private User createUserWithId() {
        User user = new User();
        user.setId(USER_ID);
        return user;
    }

    private User createUserWithIdAndRoles() {
        Set<Role> roles = new HashSet<>();
        User user = new User();
        user.setId(USER_ID);
        user.setRoles(roles);
        return user;
    }

    private Role createRoleWithType() {
        Role role = new Role();
        role.setType(RoleType.MANAGER);
        return role;
    }

    private UserUpdateRequestDto createUserUpdateRequestDto() {
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();
        requestDto.setFirstName(USER_FIRST_NAME);
        requestDto.setLastName(USER_LAST_NAME);
        requestDto.setEmail(USER_EMAIL);
        requestDto.setPassword(USER_PASSWORD);
        return requestDto;
    }

    private UserUpdateRequestDto createUserUpdateRequestDtoWithExistingEmail() {
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();
        requestDto.setEmail(EXISTING_EMAIL);
        return requestDto;
    }

    private UserResponseDto createUserResponseDto() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(USER_ID);
        return userResponseDto;
    }

    private UserResponseDto createUserResponseDto(Long userId, String email,
                                                  String firstName, String lastName) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(userId);
        userResponseDto.setEmail(email);
        userResponseDto.setFirstName(firstName);
        userResponseDto.setLastName(lastName);
        return userResponseDto;
    }

    private User createUserWithDetails(Long userId, String email, String firstName,
                                       String lastName, String password, Set<Role> roles) {
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setRoles(roles);
        return user;
    }
}
