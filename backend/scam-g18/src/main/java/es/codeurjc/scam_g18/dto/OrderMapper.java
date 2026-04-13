package es.codeurjc.scam_g18.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.scam_g18.model.Order;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface OrderMapper {
    OrderDTO toDTO(Order order);
    Order toDomain(OrderDTO orderDTO);
    List<OrderDTO> toDTOs(Collection<Order> orders);
}
