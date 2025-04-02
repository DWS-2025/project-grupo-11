package grupo11.bcf_store.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "OrderTable")
public class Order {
    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "Order_Products",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors
    public Order() {
    }

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

    public int getTotalItems() {
        return products.size();
    }

    public boolean isSimpleOrder() {
        if (this.products.isEmpty()) {
            return true;
        }

        Long firstProductId = products.get(0).getId();

        for (Product product : products) {
            if (!product.getId().equals(firstProductId)) {
                return false;
            }
        }
        
        return true;
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

    public void removeOrderFromUser() {
        if (user != null) {
            user.getOrders().remove(this);
        }
        for (Product product : products) {
            product.getOrders().remove(this);
        }
    }
}
