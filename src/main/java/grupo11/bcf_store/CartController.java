package grupo11.bcf_store;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CartController {

    private final Cart cart;
    private final Map<String, Product> products;

    @Autowired
    public CartController(Cart cart, Map<String, Product> products) {
        this.cart = cart;
        this.products = products;
    }

    @PostMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable String id) {
        Product product = products.get(id);
        if (product != null) {
            cart.addProduct(product);
            return "Product added to cart";
        } else {
            return "Product not found";
        }
    }
}
