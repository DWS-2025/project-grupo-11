package grupo11.bcf_store.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "CartTable")
public class Cart {
    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToMany
    @JoinTable(name = "Cart_Products", joinColumns = @JoinColumn(name = "cart_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products;

    @OneToOne(mappedBy = "cart")
    private User user;

    // Constructor
    public Cart() {
        this.products = new ArrayList<>();
    }

    // Methods to add and remove products
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

        for (Product product : products) {
            totalPrice += product.getPrice();
        }

        return totalPrice;
    }

    public int getTotalItems() {
        return products.size();
    }

    public void clearCart() {
        products.clear();
    }

    public void displayCartInformation() {
        for (Product product : products) {
            product.displayInformation();
            System.out.println("-----");
        }
    }

    public Order checkout() {
        return new Order(new ArrayList<>(products));
    }

    public void updateCartProduct(Product updatedProduct) {
        this.getProducts()
                .replaceAll((product) -> product.getId() == updatedProduct.getId() ? updatedProduct : product);
    }
}
