package grupo11.bcf_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import grupo11.bcf_store.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @NonNull
    Page<Product> findAll(Pageable pageable);
    
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    Page<Product> findByPrice(double price, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndPrice(String name, double price, Pageable pageable);

    Page<Product> findByDescriptionContainingIgnoreCaseAndPrice(String description, double price, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndPrice(String name, String description, double price, Pageable pageable);
}