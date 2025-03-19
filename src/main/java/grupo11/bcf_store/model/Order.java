package grupo11.bcf_store.model;
import java.util.List;

import jakarta.persistence.*;

public class Order {
    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(mappedBy = "order")
    private List<Product> products;

    // Constructor
    public Order(List<Product> products) {
        this.products = products;
    }

    // Getter and setter methods
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Method to display order information
    public void displayOrderInformation() {
        for (Product product : products) {
            product.displayInformation();
            System.out.println("-----");
        }
    }

    public void deleteOrder() {
        products.clear();
    }
}
