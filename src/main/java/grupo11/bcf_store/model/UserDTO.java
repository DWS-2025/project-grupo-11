package grupo11.bcf_store.model;

import java.util.List;

public record UserDTO(
        long id,
        String username,
        String password,
        CartSimpleDTO cart,
        List<OrderSimpleDTO> orders) {
}

