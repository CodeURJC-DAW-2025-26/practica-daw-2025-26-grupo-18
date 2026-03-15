package es.codeurjc.scam_g18.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collection;
import java.util.List;

import es.codeurjc.scam_g18.dto.OrderItemDTO;
import es.codeurjc.scam_g18.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(source = "subscription", target = "isSubscription")
    OrderItemDTO toDTO(OrderItem orderItem);

    OrderItem toDomain(OrderItemDTO orderItemDTO);

    List<OrderItemDTO> toDTOs(Collection<OrderItem> courses);
}
