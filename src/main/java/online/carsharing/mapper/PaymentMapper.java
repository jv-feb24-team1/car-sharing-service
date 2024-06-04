package online.carsharing.mapper;

import online.carsharing.config.MapperConfig;
import online.carsharing.dto.request.payment.PaymentRequestDto;
import online.carsharing.dto.response.payment.PaymentResponseDto;
import online.carsharing.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toDto(Payment payment);

    Payment toEntity(PaymentRequestDto requestDto);
}
