package grupo11.bcf_store.model.dto;

import grupo11.bcf_store.model.Cart;

public record UserSimpleDTO(
        long id,
        String username,
        String password,
        Cart cart) {
}

