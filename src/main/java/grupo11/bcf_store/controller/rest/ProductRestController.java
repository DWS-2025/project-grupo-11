package grupo11.bcf_store.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
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

import grupo11.bcf_store.model.dto.ProductDTO;
import grupo11.bcf_store.service.ProductService;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/products/")
public class ProductRestController {

	@Autowired
	private ProductService productService;

    // API methods
    @GetMapping("/")
    public Page<ProductDTO> getProducts(Pageable pageable, @RequestParam(defaultValue = "10") int size) {
        Pageable updatedPageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
        return productService.getProducts(updatedPageable);
    }

    @GetMapping("/{id}/")
	public ProductDTO getProduct(@PathVariable long id) {
        return productService.getProduct(id);
    }

    @PostMapping("/")
	public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
		ProductDTO savedProduct = productService.save(productDTO);
		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedProduct.id()).toUri();
		return ResponseEntity.created(location).body(savedProduct);
	}

    @PutMapping("/{id}/")
	public ProductDTO replaceProduct(@PathVariable long id, @RequestBody ProductDTO updatedProductDTO) {
        return productService.updateProductREST(id, updatedProductDTO);
	}

	@Transactional
    @DeleteMapping("/{id}/")
	public ProductDTO deleteProduct(@PathVariable long id) {
		ProductDTO product = productService.getProduct(id);
		if (product != null) {
			productService.removeProduct(product);
			return product;
		} else {
			throw new NoSuchElementException();
		}
	}

    @GetMapping("/search/")
    public Page<ProductDTO> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double price,
            Pageable pageable) {
        Pageable updatedPageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        return productService.searchProducts(name, description, price, updatedPageable);
    }

	// Image methods
	@PostMapping("/{id}/image/")
	public ResponseEntity<Object> createProductImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
			throws IOException {
		try {
			productService.validateImageFile(imageFile);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

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
		try {
			productService.validateImageFile(imageFile);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

		productService.replaceProductImage(id, imageFile.getInputStream(), imageFile.getSize());

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}/image/")
	public ResponseEntity<Object> deleteProductImage(@PathVariable long id) throws IOException {

		productService.deleteProductImage(id);

		return ResponseEntity.noContent().build();
	}

    // Exception handling
    @ControllerAdvice
    class NoSuchElementExceptionControllerAdvice {

        @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
        @ExceptionHandler(NoSuchElementException.class)
        public void handleNoTFound() {
        }
    }
}
