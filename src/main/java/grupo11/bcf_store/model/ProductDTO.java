package grupo11.bcf_store.model;

import java.sql.Blob;
import java.util.List;

public record ProductDTO(
        Long id,
        String name,
        double price,
        String description,
        Blob imageFile,
        String image,
        List<Order> orders) {
}
