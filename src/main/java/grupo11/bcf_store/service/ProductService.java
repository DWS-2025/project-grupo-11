package grupo11.bcf_store.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.dto.OrderDTO;
import grupo11.bcf_store.model.dto.ProductDTO;
import grupo11.bcf_store.model.mapper.OrderMapper;
import grupo11.bcf_store.model.mapper.ProductMapper;
import grupo11.bcf_store.repository.CartRepository;
import grupo11.bcf_store.repository.OrderRepository;
import grupo11.bcf_store.repository.ProductRepository;
import jakarta.annotation.Resource;
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
    private ProductMapper productMapper;

    public List<ProductDTO> getProducts() {
        return productMapper.toDTOs(productRepository.findAll());
    }

    public ProductDTO getProduct(long id) {
        return productRepository.findById(id)
                .map(productMapper::toDTO)
                .orElse(null);
    }

    public void save(ProductDTO productDTO) {
        if(productDTO != null) {
            productRepository.save(productMapper.toDomain(productDTO));
        } else {
            throw new IllegalArgumentException("ProductDTO cannot be null");
        }
    }

    public void save(Product product) {
        productRepository.save(product);
        product.setImage("http://localhost:8080/product-image/" + product.getId() + "/");
        productRepository.save(product);
    }

    @Transactional
    public void removeProduct(ProductDTO productDTO) {
        Product initialProduct = productMapper.toDomain(productDTO);
        long id = initialProduct.getId();
        
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
            cartRepository.findAll().forEach(cart -> cart.removeProduct(product));
            productRepository.delete(product);
        }
    }

    @Transactional
    public void removeProductById(long id) {
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

    public ProductDTO editProduct(long id) {
        Product product = productRepository.findById(id).orElse(new Product("", 0, "", null));
        return productMapper.toDTO(product);
    }

    public ProductDTO submitProductAdded(String name, String description, double price, MultipartFile imageFile)
            throws Exception {
        Product product = new Product();

        if (!name.isEmpty() && !description.isEmpty() && price > 0) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);

            // Save the product to generate an ID
            product = productRepository.save(product);

            if (!imageFile.isEmpty()) {
                String mimeType = imageFile.getContentType();
                if (mimeType == null || !(mimeType.equals("image/png") || mimeType.equals("image/jpg") || mimeType.equals("image/jpeg") || mimeType.equals("image/webp"))) {
                    throw new IllegalArgumentException("Solo se permiten imágenes.");
                }
                product.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
                product.setImage("http://localhost:8080/product-image/" + product.getId() + "/");
            }

            // Save the product again to update the image
            product = productRepository.save(product);
        }
        return productMapper.toDTO(product);
    }

    public ProductDTO submitProductEdited(long id, String name, String description, double price, MultipartFile imageFile)
            throws Exception {
        Product product = productRepository.findById(id).orElse(new Product());

        if (!name.isEmpty() && !description.isEmpty() && price > 0) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);

            if (!imageFile.isEmpty()) {
                String mimeType = imageFile.getContentType();
                if (mimeType == null || !(mimeType.equals("image/png") || mimeType.equals("image/jpg") || mimeType.equals("image/jpeg") || mimeType.equals("image/webp"))) {
                    throw new IllegalArgumentException("Solo se permiten imágenes.");
                }
                product.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
                product.setImage("http://localhost:8080/product-image/" + product.getId() + "/");
            }

            // Save the product
            product = productRepository.save(product);
        }
        return productMapper.toDTO(product);
    }

    public List<OrderDTO> getProductOrders(long id) {
        return productRepository.findById(id)
                .map(Product::getOrders)
                .map(orderMapper::toDTOs)
                .orElse(null);
    }

    public List<OrderDTO> getUniqueProductOrders(long id) {
        return productRepository.findById(id)
                .map(product -> product.getOrders().stream().distinct().collect(Collectors.toList()))
                .map(orderMapper::toDTOs)
                .orElse(null);
    }

    // Check if the product belongs to the user logged in
    public List<OrderDTO> getUserUniqueProductOrders(long productId, long userId) {
        return productRepository.findById(productId)
                .map(product -> product.getOrders().stream()
                        .distinct()
                        .filter(order -> order.getUser().getId() == userId)
                        .map(orderMapper::toDTO)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    public void updateCartProduct(ProductDTO updatedProductDTO) {
        Product updatedProduct = productMapper.toDomain(updatedProductDTO);
        cartRepository.findAll().forEach(cart -> {
            cart.getProducts()
                    .replaceAll(product -> product.getId() == updatedProduct.getId() ? updatedProduct : product);
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

    // Image methods
    public void createProductImage(long id, URI location, InputStream inputStream, long size) {

		Product product = productRepository.findById(id).orElseThrow();

		product.setImage(location.toString());
		product.setImageFile(BlobProxy.generateProxy(inputStream, size));
		productRepository.save(product);
	}

    public Resource getProductImageApi(long id) throws SQLException {

		Product product = productRepository.findById(id).orElseThrow();

		if (product.getImageFile() != null) {
			return (Resource) new InputStreamResource(product.getImageFile().getBinaryStream());
		} else {
			throw new NoSuchElementException();
		}
	}

	public void replaceProductImage(long id, InputStream inputStream, long size) {

		Product product = productRepository.findById(id).orElseThrow();

		if(product.getImage() == null){
			throw new NoSuchElementException();
		}

		product.setImageFile(BlobProxy.generateProxy(inputStream, size));

		productRepository.save(product);
	}

	public void deleteProductImage(long id) {

		Product product = productRepository.findById(id).orElseThrow();

		if(product.getImage() == null){
			throw new NoSuchElementException();
		}

		product.setImageFile(null);
		product.setImage(null);

		productRepository.save(product);
	}

    public byte[] getProductImage(long id) throws SQLException {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null && product.getImageFile() != null) {
            return product.getImageFile().getBytes(1, (int) product.getImageFile().length());
        }
        return null;
    }
    
    
    public Page<ProductDTO> convertToDTOPage(Page<Product> productPage) {
        return new PageImpl<>(
            productPage.getContent().stream().map(productMapper::toDTO).collect(Collectors.toList()),
            productPage.getPageable(),
            productPage.getTotalElements()
        );
    }

    public Page<ProductDTO> getProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return convertToDTOPage(productPage);
    }

    public Page<ProductDTO> findByName(String name, Pageable pageable) {
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(name, pageable);
        return convertToDTOPage(productPage);
    }

    public Page<ProductDTO> findByDescription(String description, Pageable pageable) {
        Page<Product> productPage = productRepository.findByDescriptionContainingIgnoreCase(description, pageable);
        return convertToDTOPage(productPage);
    }

    public Page<ProductDTO> findByPrice(double price, Pageable pageable) {
        Page<Product> productPage = productRepository.findByPrice(price, pageable);
        return convertToDTOPage(productPage);
    }

    public Page<ProductDTO> findByNameAndDescription(String name, String description, Pageable pageable) {
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(name, description, pageable);
        return convertToDTOPage(productPage);
    }

    public Page<ProductDTO> findByNameAndPrice(String name, double price, Pageable pageable) {
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCaseAndPrice(name, price, pageable);
        return convertToDTOPage(productPage);
    }

    public Page<ProductDTO> findByDescriptionAndPrice(String description, double price, Pageable pageable) {
        Page<Product> productPage = productRepository.findByDescriptionContainingIgnoreCaseAndPrice(description, price, pageable);
        return convertToDTOPage(productPage);
    }

    public Page<ProductDTO> findByNameAndDescriptionAndPrice(String name, String description, double price, Pageable pageable) {
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndPrice(name, description, price, pageable);
        return convertToDTOPage(productPage);
    }

    public Page<ProductDTO> searchProducts(String name, String description, Double price, Pageable pageable) {
        Page<Product> productPage;

        if (name != null && !name.isEmpty() && description != null && !description.isEmpty() && price != null) {
            productPage = productRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndPrice(name, description, price, pageable);
        } else if (name != null && !name.isEmpty() && description != null && !description.isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(name, description, pageable);
        } else if (name != null && !name.isEmpty() && price != null) {
            productPage = productRepository.findByNameContainingIgnoreCaseAndPrice(name, price, pageable);
        } else if (description != null && !description.isEmpty() && price != null) {
            productPage = productRepository.findByDescriptionContainingIgnoreCaseAndPrice(description, price, pageable);
        } else if (name != null && !name.isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (description != null && !description.isEmpty()) {
            productPage = productRepository.findByDescriptionContainingIgnoreCase(description, pageable);
        } else if (price != null) {
            productPage = productRepository.findByPrice(price, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return convertToDTOPage(productPage);
    }
}
