package es.codeurjc.scam_g18.dto;

import org.mapstruct.Mapper;
import java.util.Collection;
import java.util.List;

import es.codeurjc.scam_g18.model.OrderItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface OrderItemMapper {
    OrderItemDTO toDTO(OrderItem orderItem);

    OrderItem toDomain(OrderItemDTO orderItemDTO);

    List<OrderItemDTO> toDTOs(Collection<OrderItem> courses);
}
