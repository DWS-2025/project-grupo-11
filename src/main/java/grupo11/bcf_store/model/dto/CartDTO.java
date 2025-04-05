package grupo11.bcf_store.model.dto;

import java.util.List;

public record CartDTO(
        long id,
        List<ProductSimpleDTO> products,
        UserSimpleDTO user) {
}
