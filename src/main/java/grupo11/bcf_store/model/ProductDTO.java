package grupo11.bcf_store.model;

import java.util.List;

public record ProductDTO(
        long id,
        String name,
        double price,
        String description,
        String image,
        List<OrderSimpleDTO> orders) {
}
