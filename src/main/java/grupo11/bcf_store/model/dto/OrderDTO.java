package grupo11.bcf_store.model.dto;

import java.util.List;

public record OrderDTO(
        long id,
        List<ProductSimpleDTO> products,
        long userId) {
}
