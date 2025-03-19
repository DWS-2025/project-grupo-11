package grupo11.bcf_store.model;
import java.util.List;

import jakarta.persistence.*;

public class User {
    // Attributes
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    
    private String username;
    private String password;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    @ManyToMany
    private List<Order> orders;

    // Constructor
    public User(String id, String username, String password, List<Order> orders) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.orders = orders;
        this.cart = new Cart();
    }

    // Getter and setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Cart getCart() {
        return cart;
    }

    public void setBasket(Cart cart) {
        this.cart = cart;
    }

    // Method to display user information
    public void displayUserInformation() {
        System.out.println("Username: " + username);
        System.out.println("Orders:");
        for (Order order : orders) {
            order.displayOrderInformation();
            System.out.println("=====");
        }
        System.out.println("Basket:");
        cart.displayCartInformation();
    }

    // Method to checkout the basket and create an order
    public void checkoutBasket() {
        Order newOrder = cart.checkout();
        orders.add(newOrder);
        cart.clearCart();
    }
}
