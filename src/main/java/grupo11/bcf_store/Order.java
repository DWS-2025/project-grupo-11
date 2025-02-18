package grupo11.bcf_store;
import java.util.List;

public class Order {
    // Attributes
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

    // Method to display order information
    public void displayOrderInformation() {
        for (Product product : products) {
            product.displayInformation();
            System.out.println("-----");
        }
    }
}
