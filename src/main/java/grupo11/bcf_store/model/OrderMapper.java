package grupo11.bcf_store.model;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDTO toDTO(Order order);

    List<OrderDTO> toDTOs(List <Order> orders);

    Order toDomain(OrderDTO orderDTO);
}
