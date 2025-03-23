package grupo11.bcf_store.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.repository.CartRepository;
import grupo11.bcf_store.repository.OrderRepository;
import grupo11.bcf_store.repository.ProductRepository;
import jakarta.transaction.Transactional;


@Service
public class ProductService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    @Transactional
    public void removeProduct(Product product) {
        if(product != null) {
            for(Order order : product.getOrders()) {
                if(order.getTotalItems() == 1) {
                    orderRepository.delete(order);
                    productRepository.delete(product);
                } else {
                    productRepository.delete(product);
                }
            }
        }
    }

    public void removeProductById(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        
        if(product != null) {
            for(Order order : product.getOrders()) {
                if(order.getTotalItems() == 1) {
                    orderRepository.delete(order);
                    productRepository.delete(product);
                } else {
                    productRepository.delete(product);
                }
            }
        }
    }

    public Product editProduct(Long id) {
        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            product = new Product("", 0, "", "");
        }

        return product;
    }

    public Product submitProductAdded(String name, String description, double price, MultipartFile image) {
        Product product = new Product();
    
        if (!name.isEmpty() && !description.isEmpty() && price > 0 && !image.isEmpty()) {
            String imageUrl = "/images/" + image.getOriginalFilename();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setImageUrl(imageUrl);
            this.save(product);
        }
    
        return product;
    }
    
    public Product submitProductEdited(Long id, String name, String description, double price, MultipartFile image) {
        Product product;
        if (id != null) {
            product = productRepository.findById(id).orElse(new Product());
        } else {
            product = new Product();
        }

        if (!name.isEmpty() && !description.isEmpty() && price > 0 && !image.isEmpty()) {
            String imageUrl = "/images/" + image.getOriginalFilename();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setImageUrl(imageUrl);
            this.save(product);
        }

        return product;
    }

    public List<Order> getProductOrders(Long id) {
        return productRepository.findById(id)
                                .map(Product::getOrders)
                                .orElse(null);
    }

    public void updateCartProduct(Product updatedProduct) {
        cartRepository.findAll().forEach(cart -> {
            cart.getProducts().replaceAll(product -> 
                product.getId().equals(updatedProduct.getId()) ? updatedProduct : product
            );
        });
    }
}
