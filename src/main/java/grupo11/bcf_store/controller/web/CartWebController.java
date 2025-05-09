package grupo11.bcf_store.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import grupo11.bcf_store.model.dto.CartDTO;
import grupo11.bcf_store.model.dto.ProductDTO;
import grupo11.bcf_store.service.CartService;
import grupo11.bcf_store.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import grupo11.bcf_store.service.UserService;

@Controller
public class CartWebController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("/cart/")
    public String cart(Model model, HttpServletRequest request) {
        String username = userService.getLoggedInUsername(request);
        if (username == null) {
            model.addAttribute("errorMessage", "Usuario no autenticado.");
            return "error";
        }

        long cartId = userService.getCartIdByUsername(username); // Get cart ID for the user
        CartDTO cart = cartService.getCart(cartId);

        if (cart != null) {
            model.addAttribute("cart", cartService.getProductsInCart(cartId));
            model.addAttribute("totalItems", cartService.getTotalItemsInCart(cartId));
            model.addAttribute("totalPrice", cartService.getTotalPrice(cartId));
            return "cart";
        } else {
            model.addAttribute("errorMessage", "Carrito no encontrado.");
            return "error";
        }
    }

    @PostMapping("/add-to-cart/{productId}/")
    public String addToCart(@PathVariable long productId, Model model, HttpServletRequest request) {
        String username = userService.getLoggedInUsername(request);
        if (username == null) {
            model.addAttribute("errorMessage", "Usuario no autenticado.");
            return "error";
        }

        long cartId = userService.getCartIdByUsername(username); // Get cart ID for the user
        ProductDTO product = productService.getProduct(productId);

        if (product != null) {
            CartDTO cart = cartService.getCart(cartId);
            if (cart != null) {
                cartService.addProductToCart(cartId, product);
                return "redirect:/cart/";
            } else {
                model.addAttribute("errorMessage", "Carrito no encontrado.");
                return "error";
            }
        } else {
            model.addAttribute("errorMessage", "Producto no encontrado.");
            return "error";
        }
    }

    @PostMapping("/remove-from-cart/{productId}/")
    public String removeFromCart(@PathVariable long productId, Model model, HttpServletRequest request) {
        String username = userService.getLoggedInUsername(request);
        if (username == null) {
            model.addAttribute("errorMessage", "Usuario no autenticado.");
            return "error";
        }

        long cartId = userService.getCartIdByUsername(username); // Get cart ID for the user
        ProductDTO product = productService.getProduct(productId);

        if (product != null) {
            CartDTO cart = cartService.getCart(cartId);
            if (cart != null) {
                cartService.removeProductFromCart(cartId, product);
                return "redirect:/cart/";
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
