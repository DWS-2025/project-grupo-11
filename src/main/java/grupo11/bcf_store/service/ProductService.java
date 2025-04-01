package grupo11.bcf_store.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        if (product != null) {
            for (Order order : product.getOrders()) {
                if (order.getTotalItems() == 1 || order.isSimpleOrder()) {
                    orderRepository.delete(order);
                    productRepository.delete(product);
                } else {
                    productRepository.delete(product);
                }
            }
            cartRepository.findAll().forEach(cart -> cart.removeProduct(product));
            productRepository.delete(product);
        }
    }

    @Transactional
    public void removeProductById(Long id) {
        Product product = productRepository.findById(id).orElse(null);

        if (product != null) {
            for (Order order : product.getOrders()) {
                if (order.getTotalItems() == 1 || order.isSimpleOrder()) {
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
            product = new Product("", 0, "", null);
        }

        return product;
    }

    public Product submitProductAdded(String name, String description, double price, MultipartFile imageFile)
            throws Exception {
        Product product = new Product();

        if (!name.isEmpty() && !description.isEmpty() && price > 0 && !imageFile.isEmpty()) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            this.save(product, imageFile);
        }
        return product;
    }

    public Product submitProductEdited(Long id, String name, String description, double price, MultipartFile imageFile)
            throws Exception {
        Product product = productRepository.findById(id).orElse(new Product());

        if (!name.isEmpty() && !description.isEmpty() && price > 0) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            if (!imageFile.isEmpty()) {
                this.save(product, imageFile);
            } else if (product.getImageFile() != null) {
                this.save(product);
            }
        }
        return product;
    }

    public List<Order> getProductOrders(Long id) {
        return productRepository.findById(id)
                .map(Product::getOrders)
                .orElse(null);
    }

    public List<Order> getUniqueProductOrders(Long id) {
        return productRepository.findById(id)
                .map(product -> product.getOrders().stream().distinct().collect(Collectors.toList()))
                .orElse(null);
    }

    public void updateCartProduct(Product updatedProduct) {
        cartRepository.findAll().forEach(cart -> {
            cart.getProducts()
                    .replaceAll(product -> product.getId().equals(updatedProduct.getId()) ? updatedProduct : product);
        });
    }

    public void save(Product product, MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            product.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        this.save(product);
    }

    public Blob getImageBlob(String path) throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new Exception("Image not found: " + path);
            }
            byte[] bytes = inputStream.readAllBytes();
            return BlobProxy.generateProxy(bytes);
        }
    }

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> findByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<Product> findByDescription(String description, Pageable pageable) {
        return productRepository.findByDescriptionContainingIgnoreCase(description, pageable);
    }

    public Page<Product> findByPrice(double price, Pageable pageable) {
        return productRepository.findByPrice(price, pageable);
    }

    public Page<Product> findByNameAndDescription(String name, String description, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(name, description, pageable);
    }

    public Page<Product> findByNameAndPrice(String name, double price, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndPrice(name, price, pageable);
    }

    public Page<Product> findByDescriptionAndPrice(String description, double price, Pageable pageable) {
        return productRepository.findByDescriptionContainingIgnoreCaseAndPrice(description, price, pageable);
    }

    public Page<Product> findByNameAndDescriptionAndPrice(String name, String description, double price, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndPrice(name, description, price, pageable);
    }
}
