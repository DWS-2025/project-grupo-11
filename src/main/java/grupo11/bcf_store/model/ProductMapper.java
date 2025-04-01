package grupo11.bcf_store.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "orders", ignore = true)
    ProductDTO toDTO(Product product);

    List<ProductDTO> toDTOs(List<Product> products);

    List<Product> toDomain(List<ProductDTO> productDTOs);

    @Mapping(target = "orders", ignore = true)
    Product toDomain(ProductDTO productDTO);
}
