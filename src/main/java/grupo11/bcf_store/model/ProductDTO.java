package grupo11.bcf_store.model;

import java.util.List;

public record ProductDTO(
        Long id,
        String name,
        double price,
        String description,
        String image,
        List<Order> orders) {
}
