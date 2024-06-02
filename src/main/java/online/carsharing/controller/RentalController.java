package online.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.rental.ActualReturnDateDto;
import online.carsharing.dto.request.rental.RentalRequestDto;
import online.carsharing.dto.response.rental.RentalResponseDto;
import online.carsharing.entity.User;
import online.carsharing.service.RentalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rentals Controller", description = "Managing users' car rentals")
@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @Operation(
            summary = "Create Rental",
            description = "Adds a new rental for a user. "
                    + "Requires the rental details in the request body."
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    public RentalResponseDto createRental(
            @Valid @RequestBody RentalRequestDto requestDto
    ) {
        return rentalService.save(requestDto);
    }

    @Operation(
            summary = "Get User Rentals by Activity Status",
            description = "Retrieve rentals for a specific user based on their active status. "
                    + "If userId is provided, it retrieves rentals for the specified user. "
                    + "If userId is not provided, it retrieves rentals for the authenticated user. "
                    + "A user with the 'ROLE_MANAGER' can get any user rentals, "
                    + "while a user with the role 'ROLE_CUSTOMER' can only get their rentals"
    )
    @GetMapping("/")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    public List<RentalResponseDto> getAllUserRentalsByIsActive(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long userId,
            @RequestParam(value = "is_active") Boolean isActive
    ) {
        return rentalService.getUserRentals(user, userId, isActive);
    }

    @Operation(
            summary = "Get Rental by ID",
            description = "Retrieves details of a specific rental by its id. "
                    + "Requires the rental id in the path parameter."
    )
    @GetMapping("/{rentalId}")
    @PreAuthorize("hasRole('MANAGER')")
    public RentalResponseDto getRental(
            @PathVariable Long rentalId
    ) {
        return rentalService.getById(rentalId);
    }

    @Operation(
            summary = "Set Actual Return Date",
            description = "Updates the actual return date for a rental. "
                    + "Requires the rental id in the path parameter and "
                    + "the actual return date in the request body."
    )
    @PostMapping("/{rentalId}/return")
    @PreAuthorize("hasRole('MANAGER')")
    public RentalResponseDto setActualReturnDate(
            @PathVariable Long rentalId,
            @Valid @RequestBody ActualReturnDateDto requestDto
    ) {
        return rentalService.setActualReturnDate(rentalId, requestDto);
    }
}
