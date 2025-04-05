package grupo11.bcf_store.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "user", ignore = true)
    CartDTO toDTO(Cart cart);

    List<CartDTO> toDTOs(List<Cart> carts);

    Cart toDomain(CartDTO cartDTO);

}
