package online.carsharing.mapper;

import online.carsharing.config.MapperConfig;
import online.carsharing.dto.request.rental.RentalRequestDto;
import online.carsharing.dto.response.rental.RentalResponseDto;
import online.carsharing.entity.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "actualReturnDate", ignore = true),
            @Mapping(target = "active", ignore = true)
    })
    Rental toEntity(RentalRequestDto rentalDto);

    RentalResponseDto toDto(Rental rental);
}
