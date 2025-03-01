package grupo11.bcf_store.model;
import java.util.List;

public class Order {
// Attributes
    private List<Product> products;
    private String id;

// Constructor
    public Order(List<Product> products, String id) {
        this.products = products;
        this.id = id;
    }

    // Getter and setter methods
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
