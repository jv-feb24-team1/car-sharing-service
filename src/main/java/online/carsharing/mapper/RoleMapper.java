package online.carsharing.mapper;

import online.carsharing.config.MapperConfig;
import online.carsharing.dto.request.role.RoleChangeRequestDto;
import online.carsharing.entity.RoleType;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RoleMapper {
    default RoleType toRole(RoleChangeRequestDto dto) {
        return RoleType.valueOf(dto.getRole());
    }
}
