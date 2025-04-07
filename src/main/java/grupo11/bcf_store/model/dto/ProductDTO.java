package grupo11.bcf_store.model.dto;

import java.util.List;

public record ProductDTO(
        Long id,
        String name,
        double price,
        String description,
        String image,
        List<OrderSimpleDTO> orders) {
}
