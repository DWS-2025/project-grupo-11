package grupo11.bcf_store.model.dto;

import grupo11.bcf_store.model.Cart;

public record UserSimpleDTO(
        long id,
        String username,
        String password,
        String fullName,
        String description,
        String dniFilePath,
        Cart cart) {
}

