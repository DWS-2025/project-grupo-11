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
import grupo11.bcf_store.service.CartService;
import grupo11.bcf_store.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/carts/")
public class CartRestController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    // API methods
    @GetMapping("/")
    public List<CartDTO> getCarts(HttpServletRequest request) {
        String username = userService.getLoggedInUsername(request);
        
        if (username == null) {
            throw new SecurityException("Usuario no autenticado.");
        }

        if(cartService.isAdmin(request)) {
            return cartService.getCarts();
        } else {
            return cartService.getCartByUsername(username);
        }
    }

    @GetMapping("/{id}/")
    public CartDTO getCart(@PathVariable long id, HttpServletRequest request) {
        String username = userService.getLoggedInUsername(request);

        if(cartService.isAdmin(request)) {
            return cartService.getCart(id);
        } else {
            return cartService.cartBelongsToUser(id, username);
        }
    }

    @PostMapping("/")
    public ResponseEntity<CartDTO> createCart(@RequestBody CartDTO cartDTO) {
        Cart savedCart = cartService.saveCart(cartDTO);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedCart.getId()).toUri();

        return ResponseEntity.created(location).body(cartService.getCart(savedCart.getId()));
    }

    @PutMapping("/{id}/")
    public CartDTO replaceCart(@PathVariable long id, @RequestBody CartDTO updatedCartDTO, jakarta.servlet.http.HttpServletRequest request) {
        if (!cartService.canAccessCart(request, id)) {
            throw new SecurityException("No autorizado");
        }
        return cartService.updateCartbyId(id, updatedCartDTO);
    }

    @Transactional
    @DeleteMapping("/{id}/")
    public CartDTO deleteCart(@PathVariable long id, jakarta.servlet.http.HttpServletRequest request) {
        if (!cartService.canAccessCart(request, id)) {
            throw new SecurityException("No autorizado");
        }
        CartDTO cart = cartService.getCart(id);

        cartService.deleteCartById(id);

        return cart;
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
