package grupo11.bcf_store.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import grupo11.bcf_store.model.Cart;
import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.repository.CartRepository;
import grupo11.bcf_store.service.ProductService;
import jakarta.annotation.PostConstruct;


@Controller
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartRepository cartRepository;  

    @PostConstruct
    public void init() {
        // Inicializa un carrito vacío y guárdalo en la base de datos
        Cart cart = new Cart();
        cartRepository.save(cart);
    }

    @GetMapping("/cart/{id}")
    public String cart(@PathVariable Long id, Model model) {
        Optional<Cart> cartOptional = cartRepository.findById(id);
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            model.addAttribute("cart", cart.getProducts());
            model.addAttribute("totalItems", cart.getTotalItems());
            model.addAttribute("totalPrice", cart.getTotalPrice());
            return "cart";
        } else {
            model.addAttribute("errorMessage", "Carrito no encontrado.");
            return "error";
        }
    }

    @PostMapping("/add-to-cart/{productId}")
    public String addToCart(@PathVariable Long productId, Long cartId, Model model) {
        Product product = productService.getProduct(productId);

        if (product != null) {
            Optional<Cart> cartOptional = cartRepository.findById(cartId);
            if (cartOptional.isPresent()) {
                Cart cart = cartOptional.get();
                cart.addProduct(product);
                cartRepository.save(cart); // Guarda el carrito actualizado en la base de datos
                return "redirect:/cart/" + cartId;
            } else {
                model.addAttribute("errorMessage", "Carrito no encontrado.");
                return "error";
            }
        } else {
            model.addAttribute("errorMessage", "Producto no encontrado.");
            return "error";
        }
    }

    @PostMapping("/remove-from-cart/{productId}")
    public String removeFromCart(@PathVariable Long productId, Long cartId, Model model) {
        Product product = productService.getProduct(productId);

        if (product != null) {
            Optional<Cart> cartOptional = cartRepository.findById(cartId);
            if (cartOptional.isPresent()) {
                Cart cart = cartOptional.get();
                cart.removeProduct(product);
                cartRepository.save(cart); // Guarda el carrito actualizado en la base de datos
                return "redirect:/cart/" + cartId;
            } else {
                model.addAttribute("errorMessage", "Carrito no encontrado.");
                return "error";
            }
        } else {
            model.addAttribute("errorMessage", "Producto no encontrado.");
            return "error";
        }
    }

}
