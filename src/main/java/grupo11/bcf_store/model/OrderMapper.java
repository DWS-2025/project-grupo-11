package grupo11.bcf_store.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "user", ignore = true)
    OrderDTO toDTO(Order order);

    List<OrderDTO> toDTOs(List<Order> orders);

    Order toDomain(OrderDTO orderDTO);
}
