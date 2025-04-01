package grupo11.bcf_store.model;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartDTO toDTO(Cart cart);

    List<CartDTO> toDTOs(List <Cart> carts);

    Cart toDomain(CartDTO cartDTO);
}
