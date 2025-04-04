package grupo11.bcf_store.model;

public record ProductSimpleDTO(
        Long id,
        String name,
        double price,
        String description,
        String image) {
}
