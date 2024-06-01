package online.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.role.RoleChangeRequestDto;
import online.carsharing.dto.request.user.UserRegisterRequestDto;
import online.carsharing.dto.response.role.RoleChangeResponseDto;
import online.carsharing.dto.response.user.UserResponseDto;
import online.carsharing.entity.User;
import online.carsharing.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Controller", description = "API for managing users, including operations "
        + "for updating user roles,"
        + " retrieving profile information, and updating profile information.")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Update User Role",
            description = "Updates the role of a user identified by their ID."
                    + " Requires the new role information to be provided in the request body."
    )
    @PutMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    public RoleChangeResponseDto updateUserRoleById(
            @PathVariable Long id,
            @Valid @RequestBody RoleChangeRequestDto requestDto
    ) {
        return userService.updateUserRole(id, requestDto);
    }

    @Operation(
            summary = "Get User Profile Info",
            description = "Retrieves the profile information of the currently authenticated user."
    )
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_CUSTOMER')")
    public UserResponseDto getUserProfile(@AuthenticationPrincipal User currentUser) {
        return userService.getCurrentUserProfile(currentUser.getId());
    }

    @Operation(
            summary = "Update User Profile Info",
            description = "Updates the profile information of the currently authenticated user."
                    + " Requires the updated profile"
                    + " information to be provided in the request body."
    )

    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_CUSTOMER')")
    public UserResponseDto updateUserProfile(
            @Valid @RequestBody UserRegisterRequestDto requestDto,
            @AuthenticationPrincipal User currentUser
    ) {
        return userService.updateCurrentUserProfile(currentUser.getId(), requestDto);
    }
}
