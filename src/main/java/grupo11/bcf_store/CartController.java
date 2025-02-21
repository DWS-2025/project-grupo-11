package grupo11.bcf_store;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

        products.put("product1", new Product("1ª EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido.", "http://example.com/laptop.jpg"));
        products.put("product2", new Product("3ª EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido.", "http://example.com/smartphone.jpg"));
        products.put("product3", new Product("CHUBASQUERO NEGRO 23/24", 60, "Chubasquero del Burgos CF.", "http://example.com/laptop.jpg"));
        products.put("product4", new Product("PARKA BURGOS CF 23/24", 60, "Parka con el escudo del equipo.", "http://example.com/laptop.jpg"));
        products.put("product5", new Product("BANDERA BURGOS CF", 20, "Bandera con el escudo del equipo", "http://example.com/laptop.jpg"));
        products.put("product6", new Product("BUFANDA BURGOS CF", 20, "Bufanda con el nombre y escudo del equipo", "http://example.com/smartphone.jpg"));
        products.put("product7", new Product("BALON BLANQUINEGRO", 20, "Disfruta del balón de tu equipo", "http://example.com/smartphone.jpg"));
        products.put("product8", new Product("BABERO BLANQUINEGRO", 20, "Babero con escudo del equipo", "http://example.com/laptop.jpg"));
    }

    @GetMapping("/cart.html")
    public String cart(Model model) {
        cart.addProduct(products.get("product1"));
        model.addAttribute("totalItems", cart.getProducts().size());
        model.addAttribute("totalPrice", cart.getTotalPrice());
        model.addAttribute("price", products.get("product1").getPrice());
        model.addAttribute("imageUrl", products.get("product1").getImageUrl());
        model.addAttribute("name", products.get("product1").getName());

        return "cart";
    }

    @GetMapping("/clothes.html")
    public String clothes(Model model) {
        model.addAttribute("products", products.values());
        return "clothes";
    }

    @PostMapping("/add-to-cart/{id}")
    public ResponseEntity<String> addToCart(@PathVariable String id) {
        Product product = products.get(id);
        if (product != null) {
            cart.addProduct(product);
            return ResponseEntity.ok("Product added to cart");
        } else {
            return ResponseEntity.status(404).body("Product not found");
        }
    }
}
