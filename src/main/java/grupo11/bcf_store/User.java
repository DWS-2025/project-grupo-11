package grupo11.bcf_store;
import java.util.List;

public class User {
    // Attributes
    private String username;
    private String password;
    private List<Order> orders;

    // Constructor
    public User(String username, String password, List<Order> orders) {
        this.username = username;
        this.password = password;
        this.orders = orders;
    }

    // Getter and setter methods
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

    // Method to display user information
    public void displayUserInformation() {
        System.out.println("Username: " + username);
        System.out.println("Orders:");
        for (Order order : orders) {
            order.displayOrderInformation();
            System.out.println("=====");
        }
    }
}
