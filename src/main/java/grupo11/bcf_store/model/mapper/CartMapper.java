package grupo11.bcf_store.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import grupo11.bcf_store.model.Cart;
import grupo11.bcf_store.model.dto.CartDTO;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "user", ignore = true)
    CartDTO toDTO(Cart cart);

    List<CartDTO> toDTOs(List<Cart> carts);

    Cart toDomain(CartDTO cartDTO);
}
