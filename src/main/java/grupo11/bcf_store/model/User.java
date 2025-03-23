package grupo11.bcf_store.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "UserTable")
public class User {
    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String password;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    // Constructors
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.orders = new ArrayList<>();
        this.cart = new Cart();
    }

    // Getter and setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
