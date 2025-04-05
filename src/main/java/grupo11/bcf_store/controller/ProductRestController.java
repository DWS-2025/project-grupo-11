package grupo11.bcf_store.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.ProductDTO;
import grupo11.bcf_store.model.ProductMapper;
import grupo11.bcf_store.repository.ProductRepository;
import grupo11.bcf_store.service.ProductService;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/products/")
public class ProductRestController {

	@Autowired
	private ProductService productService;
	
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper mapper;

    // API methods
    @GetMapping("/")
    public List<ProductDTO> getProducts() {
		return toDTOs(productRepository.findAll());
	}

    @GetMapping("/{id}/")
	public ProductDTO getProduct(@PathVariable long id) {
        return toDTO(productRepository.findById(id).orElseThrow());	
}

    @PostMapping("/")
	public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {

		Product product = toDomain(productDTO);

		productRepository.save(product);

		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(product.getId()).toUri();

		return ResponseEntity.created(location).body(toDTO(product));
	}

    @PutMapping("/{id}/")
	public ProductDTO replaceProduct(@PathVariable long id, @RequestBody ProductDTO updatedProductDTO) {

		if (productRepository.existsById(id)) {

			Product updatedProduct = toDomain(updatedProductDTO);

			updatedProduct.setId(id);
			productRepository.save(updatedProduct);

			return toDTO(updatedProduct);

		} else {
			throw new NoSuchElementException();
		}
	}

	@Transactional
    @DeleteMapping("/{id}/")
	public ProductDTO deleteProduct(@PathVariable long id) {

		Product product = productRepository.findById(id).orElseThrow();

		productRepository.deleteById(id);

		return toDTO(product);
	}


	// Image methods
	@PostMapping("/{id}/image/")
	public ResponseEntity<Object> createProductImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
			throws IOException {
		String location_string = "http://localhost:8080/product-image/" + id + "/";
		URI location = URI.create(location_string);

		productService.createProductImage(id, location, imageFile.getInputStream(), imageFile.getSize());

		return ResponseEntity.created(location).build();

	}

	@GetMapping("/{id}/image/")
	public ResponseEntity<Object> getProductImage(@PathVariable long id) throws SQLException, IOException {

		byte[] productImage = productService.getProductImage(id);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(productImage);

	}

	@PutMapping("/{id}/image/")
	public ResponseEntity<Object> replaceProductImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
			throws IOException {

		productService.replaceProductImage(id, imageFile.getInputStream(), imageFile.getSize());

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}/image/")
	public ResponseEntity<Object> deleteProductImage(@PathVariable long id) throws IOException {

		productService.deleteProductImage(id);

		return ResponseEntity.noContent().build();
	}


    // DTO methods
    private ProductDTO toDTO(Product product){
		return mapper.toDTO(product);
	}

	private Product toDomain(ProductDTO productDTO){
		return mapper.toDomain(productDTO);
	}

	private List<ProductDTO> toDTOs(List<Product> products){
		return mapper.toDTOs(products);
	}
        
    // Exception handling
    @ControllerAdvice
    class NoSuchElementExceptionControllerAdvice {

        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(NoSuchElementException.class)
        public void handleNoTFound() {
        }
    }
}
