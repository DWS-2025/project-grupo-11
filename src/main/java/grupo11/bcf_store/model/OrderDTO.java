package grupo11.bcf_store.model;

import java.util.List;

public record OrderDTO(
        long id,
        List<ProductSimpleDTO> products,
        UserSimpleDTO user) {
}
