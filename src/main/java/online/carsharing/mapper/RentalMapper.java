package online.carsharing.mapper;

import online.carsharing.config.MapperConfig;
import online.carsharing.dto.request.rental.RentalRequestDto;
import online.carsharing.entity.Rental;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    Rental toEntity(RentalRequestDto rentalDto);

    RentalRequestDto toDto(Rental rental);
}
