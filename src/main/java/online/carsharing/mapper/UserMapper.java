package online.carsharing.mapper;

import online.carsharing.config.MapperConfig;
import online.carsharing.dto.request.user.UserRegisterRequestDto;
import online.carsharing.dto.response.user.UserResponseDto;
import online.carsharing.entity.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toUser(UserRegisterRequestDto registerRequestDto);

    UserResponseDto toDto(User user);
}
