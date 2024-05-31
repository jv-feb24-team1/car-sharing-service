package online.carsharing.mapper;

import online.carsharing.config.MapperConfig;
import online.carsharing.dto.request.rental.RentalRequestDto;
import online.carsharing.entity.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {

    @Mappings({
            @Mapping(source = "carId", target = "car.id"),
            @Mapping(source = "userId", target = "user.id"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "actualReturnDate", ignore = true)
    })
    Rental toEntity(RentalRequestDto rentalDto);

    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "user.id", target = "userId")
    RentalRequestDto toDto(Rental rental);
}
