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
    private long id;

    private String username;
    private String password;

    private String fullName;
    private String description;

    private String dniFilePath;
    private String dniOriginalFilename;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;

    // Constructors
    public User() {
    }

    public User(String username, String password, String... roles) {
        this.username = username;
        this.password = password;
        this.roles = List.of(roles);
        this.orders = new ArrayList<>();
        this.cart = new Cart(); // Ensure a new Cart is created for the user
    }

    // Getter and setter methods
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDniFilePath() {
        return dniFilePath;
    }

    public void setDniFilePath(String dniFilePath) {
        this.dniFilePath = dniFilePath;
    }

    public String getDniOriginalFilename() {
        return dniOriginalFilename;
    }

    public void setDniOriginalFilename(String dniOriginalFilename) {
        this.dniOriginalFilename = dniOriginalFilename;
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

    public void setCart(Cart cart) {
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
        System.out.println("Cart:");
        cart.displayCartInformation();
    }

    // Method to checkout the cart and create an order
    public void checkoutCart() {
        Order newOrder = cart.checkout();
        orders.add(newOrder);
        cart.clearCart();
    }
}
