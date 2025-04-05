package grupo11.bcf_store.model.mapper;

import org.mapstruct.Mapper;

import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.dto.ProductDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDTO(Product product);

    List<Product> toDomain(List<ProductDTO> productDTOs);
    List<ProductDTO> toDTOs(List<Product> products);

    Product toDomain(ProductDTO productDTO);
}
