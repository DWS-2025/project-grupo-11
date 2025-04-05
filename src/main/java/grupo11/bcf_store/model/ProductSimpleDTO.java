package grupo11.bcf_store.model;

public record ProductSimpleDTO(
        long id,
        String name,
        double price,
        String description,
        String image) {
}
