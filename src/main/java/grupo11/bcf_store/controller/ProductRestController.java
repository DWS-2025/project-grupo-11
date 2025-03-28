package grupo11.bcf_store.controller;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.ProductDTO;
import grupo11.bcf_store.model.ProductMapper;
import grupo11.bcf_store.repository.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductRestController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper mapper;

    // API methods
    @GetMapping("/")
    public List<ProductDTO> getProducts() {
		
		return toDTOs(productRepository.findAll());
	}

    @GetMapping("/{id}")
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

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
	public ProductDTO deleteProduct(@PathVariable long id) {

		Product product = productRepository.findById(id).orElseThrow();

		productRepository.deleteById(id);

		return toDTO(product);
	}

    // DTO methods
    private ProductDTO toDTO(Product product){
		return mapper.toDTO(product);
	}

	private Product toDomain(ProductDTO postDTO){
		return mapper.toDomain(postDTO);
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
