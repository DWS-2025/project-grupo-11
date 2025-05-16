package grupo11.bcf_store.model.dto;

import java.util.List;

public record UserDTO(
        long id,
        String username,
        String password,
        String fullName,
        String description,
        String dniFilePath,
        CartSimpleDTO cart,
        List<OrderSimpleDTO> orders) {
}

