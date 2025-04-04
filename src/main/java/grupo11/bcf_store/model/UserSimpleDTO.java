package grupo11.bcf_store.model;

public record UserSimpleDTO(
        Long id,
        String username,
        String password,
        Cart cart) {
}

