package grupo11.bcf_store.model;

import java.util.List;

public record CartDTO(
        Long id,
        List<Product> products,
        User user) {
}
