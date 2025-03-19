package grupo11.bcf_store.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.repository.CartRepository;
import grupo11.bcf_store.repository.ProductRepository;


@Service
public class ProductService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductRepository productRepository;

    //private final AtomicInteger productIdCounter;

    public ProductService() {
        /*int maxIdProduct = products.keySet().stream().mapToInt(Integer::parseInt).max().orElse(0);
        this.productIdCounter = new AtomicInteger(maxIdProduct + 1);*/
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void put(Product product) {
        productRepository.save(product);
    }

    public void removeProduct(Product product) {
        if(product != null) {
            productRepository.delete(product);
        }
    }

    public void removeProductById(Long id) {
        if(productRepository.findById(id) != null) {
            productRepository.deleteById(id);
        }
    }

    /*public int getAndIncrement() {
        return productIdCounter.getAndIncrement();
    }*/

    public Product editProduct(Long id) {
        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            product = new Product("", 0, "", "");
        }

        return product;
    }

    public Product submitProduct(String name, String description, double price, MultipartFile image) {
        if (!name.isEmpty() && !description.isEmpty() && price > 0 && !image.isEmpty()) {
            String imageUrl = "/images/" + image.getOriginalFilename();
            Product product = new Product(name, price, description, imageUrl);
            this.put(product);
        }

        return null;
    }

    public List<Order> getProductOrders(Product product) {
        return product.getOrders();
    }

    public void updateCartProduct(Product updatedProduct) {
        cartRepository.findAll().forEach(cart -> {
            cart.getProducts().replaceAll(product -> 
                product.getId().equals(updatedProduct.getId()) ? updatedProduct : product
            );
        });
    }
}
