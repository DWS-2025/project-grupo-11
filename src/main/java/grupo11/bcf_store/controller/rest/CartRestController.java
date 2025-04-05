package grupo11.bcf_store.controller.rest;

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

import grupo11.bcf_store.model.Cart;
import grupo11.bcf_store.model.dto.CartDTO;
import grupo11.bcf_store.model.mapper.CartMapper;
import grupo11.bcf_store.repository.CartRepository;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/carts/")
public class CartRestController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartMapper mapper;

    // API methods
    @GetMapping("/")
    public List<CartDTO> getCarts() {
        return toDTOs(cartRepository.findAll());
    }

    @GetMapping("/{id}/")
    public CartDTO getCart(@PathVariable long id) {
        return toDTO(cartRepository.findById(id).orElseThrow());
    }

    @PostMapping("/")
    public ResponseEntity<CartDTO> createCart(@RequestBody CartDTO cartDTO) {
        Cart cart = toDomain(cartDTO);

        cartRepository.save(cart);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(cart.getId()).toUri();

        return ResponseEntity.created(location).body(toDTO(cart));
    }

    @PutMapping("/{id}/")
    public CartDTO replaceCart(@PathVariable long id, @RequestBody CartDTO updatedCartDTO) {
        if (cartRepository.existsById(id)) {
            Cart updatedCart = toDomain(updatedCartDTO);

            updatedCart.setId(id);
            cartRepository.save(updatedCart);

            return toDTO(updatedCart);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Transactional
    @DeleteMapping("/{id}/")
    public CartDTO deleteCart(@PathVariable long id) {
        Cart cart = cartRepository.findById(id).orElseThrow();

        cartRepository.deleteById(id);

        return toDTO(cart);
    }

    // DTO methods
    private CartDTO toDTO(Cart cart) {
        return mapper.toDTO(cart);
    }

    private Cart toDomain(CartDTO cartDTO) {
        return mapper.toDomain(cartDTO);
    }

    private List<CartDTO> toDTOs(List<Cart> carts) {
        return mapper.toDTOs(carts);
    }

    // Exception handling
    @ControllerAdvice
    class NoSuchElementExceptionControllerAdvice {

        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(NoSuchElementException.class)
        public void handleNotFound() {
        }
    }
}
