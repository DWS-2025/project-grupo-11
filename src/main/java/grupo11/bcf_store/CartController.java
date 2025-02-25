package grupo11.bcf_store;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final Map<String, Order> orders;
    private final Map<String, Product> products;
    private final AtomicInteger productIdCounter;
    private final AtomicInteger orderIdCounter;

    public CartController(Cart cart, Map<String, Product> products, Map<String, Order> orders) {
        this.cart = cart;
        this.products = products;
        this.orders = orders;

        products.put("1", new Product("1", "1ª EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido.", "/images/shirt0.png"));
        products.put("2", new Product("2", "3º EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido.", "/images/shirt1.png"));
        products.put("3", new Product("3", "CHUBASQUERO NEGRO 23/24", 60, "Chubasquero del Burgos CF.", "/images/chubasquero.jpg"));
        products.put("4", new Product("4", "PARKA BURGOS CF 23/24", 60, "Parka con el escudo del equipo.", "/images/parka.jpg"));
        products.put("5", new Product("5", "BUFANDA BURGOS CF", 20, "Bufanda con el nombre y escudo del equipo", "/images/scarf.jpg"));
        products.put("6", new Product("6", "BALON BLANQUINEGRO", 20, "Disfruta del balón de tu equipo", "/images/balon.jpg"));
        products.put("7", new Product("7", "BABERO BLANQUINEGRO", 20, "Babero con escudo del equipo", "/images/babero.jpg"));

        int maxIdProduct = products.keySet().stream().mapToInt(Integer::parseInt).max().orElse(0);
        this.productIdCounter = new AtomicInteger(maxIdProduct + 1);
        int maxIdOrder = orders.keySet().stream().mapToInt(Integer::parseInt).max().orElse(0);
        this.orderIdCounter = new AtomicInteger(maxIdOrder + 1);
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

    @GetMapping("/add-product")
    public RedirectView addProduct() {
        String newProductId = String.valueOf(productIdCounter.getAndIncrement());
        return new RedirectView("/add-product/" + newProductId);
    }

    @GetMapping("/add-product/{id}")
    public String editProduct(@PathVariable String id, Model model) {
        Product product = products.get(id);
        if (product == null) {
            product = new Product(id, "", 0, "", "");
        }
        model.addAttribute("product", product);
        return "add";
    }

    @PostMapping("/add-product")
    public String submitProduct(@RequestParam("id") String id,
                                @RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam("price") double price,
                                @RequestParam("image") MultipartFile image) {
        if (name.isEmpty() || description.isEmpty() || price <= 0 || image.isEmpty()) {
            return "redirect:/error.html";
        }
        String imageUrl = "/images/" + image.getOriginalFilename();
        Product product = new Product(id, name, price, description, imageUrl);
        products.put(id, product);
        updateCartProduct(product);
        return "redirect:/clothes.html";
    }

    private void updateCartProduct(Product updatedProduct) {
        cart.getProducts().replaceAll((product) -> 
            product.getId().equals(updatedProduct.getId()) ? updatedProduct : product
        );
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

    @PostMapping("/create-order")
    public RedirectView createOrder() {
        if (cart.getProducts() != null) {
            String id = String.valueOf(orderIdCounter.getAndIncrement());
            orders.put(id, new Order(cart.getProducts(), id));
            return new RedirectView("/view-order/" + id);
        } else {
            return new RedirectView("/error.html");
        }
    }

    @GetMapping("/view-order/{id}")
    public String viewOrder(@PathVariable String id, Model model) {
        Order order = orders.get(id);

        if (order.getProducts() != null && order.getProducts().size() > 0) {
            model.addAttribute("order", order);
            model.addAttribute("products", order.getProducts());
            return "viewOrder";
        } else {
            return "redirect:/error";
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
            return "redirect:/error";
        }
    }

    @GetMapping("/myaccount.html")
    public String myAccount(Model model) {
        model.addAttribute("orders", orders.values());
        return "myaccount";
    }
}
