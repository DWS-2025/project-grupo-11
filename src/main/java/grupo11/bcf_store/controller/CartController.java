package grupo11.bcf_store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import grupo11.bcf_store.model.Cart;
import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.service.CartService;
import grupo11.bcf_store.service.ProductService;

@Controller
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public String cart(Model model) {
        Long cartId = 1L; // Default cart ID
        Cart cart = cartService.getCart(cartId);

        if (cart != null) {
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
    public String addToCart(@PathVariable Long productId, Model model) {
        Long cartId = 1L; // Default cart ID
        Product product = productService.getProduct(productId);

        if (product != null) {
            Cart cart = cartService.getCart(cartId);
            if (cart != null) {
                cart.addProduct(product);
                cartService.saveCart(cart); // Save the updated cart to the database
                return "redirect:/cart";
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
    public String removeFromCart(@PathVariable Long productId, Model model) {
        Long cartId = 1L; // Default cart ID
        Product product = productService.getProduct(productId);

        if (product != null) {
            Cart cart = cartService.getCart(cartId);
            if (cart != null && cart.getProducts().contains(product)) {
                cart.removeProduct(product);
                cartService.saveCart(cart); // Save the cart even if it's empty
                return "redirect:/cart";
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
