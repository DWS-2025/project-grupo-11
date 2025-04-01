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

import grupo11.bcf_store.model.CartMapper;
import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.OrderMapper;
import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.ProductMapper;
import grupo11.bcf_store.model.CartDTO;
import grupo11.bcf_store.model.OrderDTO;
import grupo11.bcf_store.model.ProductDTO;
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

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public List<ProductDTO> getProducts() {
        return productMapper.toDTOs(productRepository.findAll());
    }

    public ProductDTO getProduct(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDTO)
                .orElse(null);
    }

    public void save(ProductDTO productDTO) {
        productRepository.save(productMapper.toDomain(productDTO));
    }

    @Transactional
    public void removeProduct(ProductDTO productDTO) {
        Product product = productMapper.toDomain(productDTO);
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

    public ProductDTO editProduct(Long id) {
        Product product = productRepository.findById(id).orElse(new Product("", 0, "", null));
        return productMapper.toDTO(product);
    }

    public ProductDTO submitProductAdded(String name, String description, double price, MultipartFile imageFile)
            throws Exception {
        Product product = new Product();

        if (!name.isEmpty() && !description.isEmpty() && price > 0 && !imageFile.isEmpty()) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            this.save(productMapper.toDTO(product), imageFile);
        }
        return productMapper.toDTO(product);
    }

    public ProductDTO submitProductEdited(Long id, String name, String description, double price, MultipartFile imageFile)
            throws Exception {
        Product product = productRepository.findById(id).orElse(new Product());

        if (!name.isEmpty() && !description.isEmpty() && price > 0) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            if (!imageFile.isEmpty()) {
                this.save(productMapper.toDTO(product), imageFile);
            } else if (product.getImageFile() != null) {
                this.save(productMapper.toDTO(product));
            }
        }
        return productMapper.toDTO(product);
    }

    public List<OrderDTO> getProductOrders(Long id) {
        return productRepository.findById(id)
                .map(Product::getOrders)
                .map(orderMapper::toDTOs)
                .orElse(null);
    }

    public List<OrderDTO> getUniqueProductOrders(Long id) {
        return productRepository.findById(id)
                .map(product -> product.getOrders().stream().distinct().collect(Collectors.toList()))
                .map(orderMapper::toDTOs)
                .orElse(null);
    }

    public void updateCartProduct(ProductDTO updatedProductDTO) {
        Product updatedProduct = productMapper.toDomain(updatedProductDTO);
        cartRepository.findAll().forEach(cart -> {
            cart.getProducts()
                    .replaceAll(product -> product.getId().equals(updatedProduct.getId()) ? updatedProduct : product);
        });
    }

    public void save(ProductDTO productDTO, MultipartFile imageFile) throws IOException {
        Product product = productMapper.toDomain(productDTO);
        if (!imageFile.isEmpty()) {
            product.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        this.save(productMapper.toDTO(product));
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

<<<<<<< HEAD
    public List<ProductDTO> findByName(String name) {
        return productMapper.toDTOs(productRepository.findByNameContainingIgnoreCase(name));
    }

    public List<ProductDTO> findByDescription(String description) {
        return productMapper.toDTOs(productRepository.findByDescriptionContainingIgnoreCase(description));
    }

    public List<ProductDTO> findByPrice(double price) {
        return productMapper.toDTOs(productRepository.findByPrice(price));
    }

    public List<ProductDTO> findByNameAndDescription(String name, String description) {
        return productMapper.toDTOs(productRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(name, description));
    }

    public List<ProductDTO> findByNameAndPrice(String name, double price) {
        return productMapper.toDTOs(productRepository.findByNameContainingIgnoreCaseAndPrice(name, price));
    }

    public List<ProductDTO> findByDescriptionAndPrice(String description, double price) {
        return productMapper.toDTOs(productRepository.findByDescriptionContainingIgnoreCaseAndPrice(description, price));
    }

    public List<ProductDTO> findByNameAndDescriptionAndPrice(String name, String description, double price) {
        return productMapper.toDTOs(productRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndPrice(name, description, price));
    }

    public Page<ProductDTO> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toDTO);
=======
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
>>>>>>> b363d389d68a2b088bd082b0c0fb733d11907c97
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
