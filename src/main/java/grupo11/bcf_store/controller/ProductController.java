package grupo11.bcf_store.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.repository.ProductRepository;
import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.service.ProductService;


@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

	@Autowired
	private ProductService productService;

    @GetMapping("/clothes")
    public String clothes(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "clothes";
    }

    @GetMapping("/add-product")
    public String addProduct() {
        return "redirect:/add-product/";
    }

    @PostMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/clothes";
    }

    @GetMapping("/add-product/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            model.addAttribute("product", productOptional.get());
            return "add";
        } else {
            model.addAttribute("errorMessage", "Producto no encontrado.");
            return "error";
        }
    }

    @PostMapping("/add-product")
    public String submitProduct(@RequestParam("id") Long id,
                                @RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam("price") double price,
                                @RequestParam("image") MultipartFile image, Model model) {
        if (!name.isEmpty() && !description.isEmpty() && price > 0 && !image.isEmpty()) {
            Product product = new Product(name, price, description, image.getOriginalFilename());
            productRepository.save(product);
            return "redirect:/clothes";
        } else {
            model.addAttribute("errorMessage", "Error al añadir producto. Por favor, rellene todos los campos.");
            return "error";
        }
    }

    @GetMapping("/view/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            List<Order> productOrders = productService.getProductOrders(product);
            model.addAttribute("product", product);
            if (productOrders != null && !productOrders.isEmpty()) {
                model.addAttribute("productOrders", productOrders);
            }
            return "view";
        } else {
            model.addAttribute("errorMessage", "Producto no encontrado.");
            return "error";
        }
    }
}
