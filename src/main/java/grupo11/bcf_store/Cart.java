package grupo11.bcf_store;
import java.util.ArrayList;
import java.util.List;


public class Cart {
    // Attributes
    private List<Product> products;

    // Constructor
    public Cart() {
        this.products = new ArrayList<>();
    }

    // Methods to add and remove products
    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public List<Product> getProducts() {
        return products;
    }

    public double getTotalPrice() {
        double totalPrice = 0;

        for(Product product:products) {
            totalPrice += product.getPrice();
        }

        return totalPrice;
    }

    public int getTotalItems() {
        return products.size();
    }

    // Method to clear the basket
    public void clearCart() {
        products.clear();
    }

    // Method to display basket information
    public void displayCartInformation() {
        for (Product product : products) {
            product.displayInformation();
            System.out.println("-----");
        }
    }

    // Method to convert basket to order
    public Order checkout() {
        return new Order(new ArrayList<>(products), "100");
    }
}
