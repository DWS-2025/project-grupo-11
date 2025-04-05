package grupo11.bcf_store.model;

public record UserSimpleDTO(
        long id,
        String username,
        String password,
        Cart cart) {
}

