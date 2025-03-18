package grupo11.bcf_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import grupo11.bcf_store.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
        
}