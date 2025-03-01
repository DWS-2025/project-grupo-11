package grupo11.bcf_store.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.Product;


@Service
public class ProductService {
    
    private final Map<String, Product> products = new HashMap<>();
    private final AtomicInteger productIdCounter;

    public ProductService() {
        products.put("1", new Product("1", "1ª EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido.", "/images/shirt0.png"));
        products.put("2", new Product("2", "3º EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido.", "/images/shirt1.png"));
        products.put("3", new Product("3", "CHUBASQUERO NEGRO 23/24", 60, "Chubasquero del Burgos CF.", "/images/chubasquero.jpg"));
        products.put("4", new Product("4", "PARKA BURGOS CF 23/24", 60, "Parka con el escudo del equipo.", "/images/parka.jpg"));
        products.put("5", new Product("5", "BUFANDA BURGOS CF", 20, "Bufanda con el nombre y escudo del equipo", "/images/scarf.jpg"));
        products.put("6", new Product("6", "BALON BLANQUINEGRO", 20, "Disfruta del balón de tu equipo", "/images/balon.jpg"));
        products.put("7", new Product("7", "BABERO BLANQUINEGRO", 20, "Babero con escudo del equipo", "/images/babero.jpg"));

        int maxIdProduct = products.keySet().stream().mapToInt(Integer::parseInt).max().orElse(0);
        this.productIdCounter = new AtomicInteger(maxIdProduct + 1);
    }

    public Map<String, Product> getProducts() {
        return products;
    }

    public Product getProduct(String id) {
        return products.get(id);
    }

    public void put(String id, Product product) {
        products.put(id, product);
    }

    public void removeProduct(Product product) {
        if(product != null) {
            String id = product.getId();
            products.remove(id);
        }
    }

    public void removeProduct(String id) {
        if(this.getProduct(id) != null) {
            products.remove(id);
        }
    }

    public int getAndIncrement() {
        return productIdCounter.getAndIncrement();
    }

    public Product editProduct(String id) {
        Product product = this.getProduct(id);

        if (product == null) {
            product = new Product(id, "", 0, "", "");
        }

        return product;
    }

    public Product submitProduct(String id, String name, String description, double price, MultipartFile image) {
        if (!name.isEmpty() && !description.isEmpty() && price > 0 && !image.isEmpty()) {
            String imageUrl = "/images/" + image.getOriginalFilename();
            Product product = new Product(id, name, price, description, imageUrl);
            this.put(id, product);
        }

        return this.getProduct(id);
    }

    public List<Order> getProductOrders(Product product) {
        return product.getOrders();
    }
}
