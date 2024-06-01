package online.carsharing.mapper;

import online.carsharing.config.MapperConfig;
import online.carsharing.dto.request.role.RoleChangeRequestDto;
import online.carsharing.entity.Role;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RoleMapper {
    Role toRole(RoleChangeRequestDto dto);
}
