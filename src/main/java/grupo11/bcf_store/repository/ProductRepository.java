package grupo11.bcf_store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import grupo11.bcf_store.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByDescriptionContainingIgnoreCase(String description);

    List<Product> findByPrice(double price);

    List<Product> findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(String name, String description);

    List<Product> findByNameContainingIgnoreCaseAndPrice(String name, double price);

    List<Product> findByDescriptionContainingIgnoreCaseAndPrice(String description, double price);

    List<Product> findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndPrice(String name, String description, double price);
}