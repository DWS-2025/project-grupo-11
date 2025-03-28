package grupo11.bcf_store.model;

import java.sql.Blob;

public record ProductDTO(
        Long id,
        String name,
        double price,
        String description,
        Blob imageFile) {
}
