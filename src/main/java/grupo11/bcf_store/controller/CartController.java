package grupo11.bcf_store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.service.CartService;
import grupo11.bcf_store.service.ProductService;


@Controller
public class CartController {

    @Autowired
	private CartService cartService;

	@Autowired
	private ProductService productService;

    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("cart", cartService.getProducts());
        model.addAttribute("totalItems", cartService.getTotalItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());

        return "cart";
    }

    @PostMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable String id, Model model) {
        Product product = productService.getProduct(id);

        if (product != null) {
            cartService.addProduct(product);
            return "redirect:/cart";
        } else {
            model.addAttribute("errorMessage", "Producto no encontrado.");
            return "error";
        }
    }

    @PostMapping("/remove-from-cart/{id}")
    public String removeFromCart(@PathVariable String id, Model model) {
        Product product = productService.getProduct(id);

        if (product != null) {
            cartService.removeProduct(product);
            return "redirect:/cart";
        } else {
            model.addAttribute("errorMessage", "Producto no encontrado.");
            return "error";
        }
    }
}
