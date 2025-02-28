package grupo11.bcf_store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.service.CartService;
import grupo11.bcf_store.service.ProductService;


@Controller
public class ProductController {
    @Autowired
	private CartService cartService;

	@Autowired
	private ProductService productService;

    @GetMapping("/clothes")
    public String clothes(Model model) {
        model.addAttribute("products", productService.getProducts().values());
        return "clothes";
    }

    @GetMapping("/add-product")
    public String addProduct() {
        String newProductId = String.valueOf(productService.getAndIncrement());
        return ("add-product/" + newProductId);
    }

    @PostMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable String id) {
        productService.removeProduct(id);
        return ("clothes");
    }

    @GetMapping("/add-product/{id}")
    public String editProduct(@PathVariable String id, Model model) {
        Product product = productService.editProduct(id);

        model.addAttribute("product", product);
        return "add";
    }

    @PostMapping("/add-product")
    public String submitProduct(@RequestParam("id") String id,
                                @RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam("price") double price,
                                @RequestParam("image") MultipartFile image, Model model) {
        if (!name.isEmpty() || description.isEmpty() || price <= 0 || image.isEmpty()) {
            Product product = productService.submitProduct(id, name, description, price, image);
            cartService.updateCartProduct(product);
            return "clothes";
        } else {
            model.addAttribute("errorMessage", "Error al añadir producto. Por favor, rellene todos los campos.");
            return "error";
        }  
    }
}
