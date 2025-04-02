package grupo11.bcf_store.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "ProductTable")
public class Product {
    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private double price;
    private String description;
    private Blob imageFile; 

    @ManyToMany(mappedBy = "products")
    private List<Order> orders; // Orders that contain this product

    // Constructors
    public Product() {
        this.orders = new ArrayList<>();
    }

    public Product(String name, double price, String description, Blob imageFile) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageFile = imageFile;
        this.orders = new ArrayList<>();
    }

    // Getter and setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Blob getImageFile() {
        return imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    // Method to display product information
    public void displayInformation() {
        System.out.println("Name: " + name);
        System.out.println("Price: " + price);
        System.out.println("Description: " + description);
        System.out.println("Image: " + imageFile);
    }
}
