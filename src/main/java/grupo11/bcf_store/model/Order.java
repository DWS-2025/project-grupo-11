package grupo11.bcf_store.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "OrderTable")
public class Order {
    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTotalItems() {
        return products.size();
    }

    public boolean isSimpleOrder() {
        if (this.products.isEmpty()) {
            return true;
        }

        long firstProductId = products.get(0).getId();

        for (Product product : products) {
            if (product.getId() != firstProductId) {
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
