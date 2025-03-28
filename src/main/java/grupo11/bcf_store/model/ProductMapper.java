package grupo11.bcf_store.model;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDTO(Product product);

    List<ProductDTO> toDTOs(List <Product> products);

    Product toDomain(ProductDTO productDTO);
}
