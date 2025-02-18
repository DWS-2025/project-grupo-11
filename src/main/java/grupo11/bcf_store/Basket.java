package grupo11.bcf_store;
import java.util.ArrayList;
import java.util.List;

public class Basket {
    // Attributes
    private List<Product> products;

    // Constructor
    public Basket() {
        this.products = new ArrayList<>();
    }

    // Methods to add and remove products
    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    // Method to clear the basket
    public void clearBasket() {
        products.clear();
    }

    // Method to display basket information
    public void displayBasketInformation() {
        for (Product product : products) {
            product.displayInformation();
            System.out.println("-----");
        }
    }

    // Method to convert basket to order
    public Order checkout() {
        return new Order(new ArrayList<>(products));
    }
}
