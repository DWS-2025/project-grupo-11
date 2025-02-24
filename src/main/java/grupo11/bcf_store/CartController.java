package grupo11.bcf_store;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class CartController {

    private final Cart cart;
    private final Map<String, Product> products;
    private final AtomicInteger productIdCounter;

    @Autowired
    public CartController(Cart cart, Map<String, Product> products) {
        this.cart = cart;
        this.products = products;

        products.put("1", new Product("1", "1ª EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido.", "/images/shirt0.png"));
        products.put("2", new Product("2", "EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido.", "/images/shirt1.png"));
        products.put("3", new Product("3", "CHUBASQUERO NEGRO 23/24", 60, "Chubasquero del Burgos CF.", "/images/chubasquero.jpg"));
        products.put("4", new Product("4", "PARKA BURGOS CF 23/24", 60, "Parka con el escudo del equipo.", "/images/parka.jpg"));
        products.put("5", new Product("5", "BUFANDA BURGOS CF", 20, "Bufanda con el nombre y escudo del equipo", "/images/scarf.jpg"));
        products.put("6", new Product("6", "BALON BLANQUINEGRO", 20, "Disfruta del balón de tu equipo", "/images/balon.jpg"));
        products.put("7", new Product("7", "BABERO BLANQUINEGRO", 20, "Babero con escudo del equipo", "/images/babero.jpg"));

        int maxId = products.keySet().stream().mapToInt(Integer::parseInt).max().orElse(0);
        this.productIdCounter = new AtomicInteger(maxId + 1);
    }

    @GetMapping("/cart.html")
    public String cart(Model model) {
        model.addAttribute("cart", cart.getProducts());
        model.addAttribute("totalItems", cart.getTotalItems());
        model.addAttribute("totalPrice", cart.getTotalPrice());

        return "cart";
    }

    @GetMapping("/clothes.html")
    public String clothes(Model model) {
        model.addAttribute("products", products.values());
        return "clothes";
    }

    @PostMapping("/add-product")
    public String submitProduct(@RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam("price") double price,
                                @RequestParam("image") MultipartFile image) {
        String id = String.valueOf(productIdCounter.getAndIncrement());
        String imageUrl = "/images/" + image.getOriginalFilename();
        Product newProduct = new Product(id, name, price, description, imageUrl);
        products.put(id, newProduct);
        return "redirect:/clothes.html";
    }

    @PostMapping("/add-to-cart/{id}")
    public RedirectView addToCart(@PathVariable String id) {
        Product product = products.get(id);

        if (product != null) {
            cart.addProduct(product);
            return new RedirectView("/cart.html");
        } else {
            return new RedirectView("/error.html");
        }
    }

    @PostMapping("/remove-from-cart/{id}")
    public RedirectView removeFromCart(@PathVariable String id) {
        Product product = products.get(id);

        if (product != null) {
            cart.removeProduct(product);
            return new RedirectView("/cart.html");
        } else {
            return new RedirectView("/error.html");
        }
    }

    @PostMapping("/delete-product/{id}")
    public RedirectView deleteProduct(@PathVariable String id) {
        Product product = products.remove(id);
        if (product != null) {
            cart.removeProduct(product);
        }
        return new RedirectView("/clothes.html");
    }

    @GetMapping("/view/{id}")
    public String viewProduct(@PathVariable String id, Model model) {
        Product product = products.get(id);

        if (product != null) {
            model.addAttribute("product", product);
            return "view";
        } else {
            return "redirect:/error"; // Redirige a una página de error si el producto no se encuentra
        }
    }
}
