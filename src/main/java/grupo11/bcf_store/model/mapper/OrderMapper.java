package grupo11.bcf_store.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.dto.OrderDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "user", ignore = true)
    OrderDTO toDTO(Order order);

    List<OrderDTO> toDTOs(List<Order> orders);

    Order toDomain(OrderDTO orderDTO);
}
