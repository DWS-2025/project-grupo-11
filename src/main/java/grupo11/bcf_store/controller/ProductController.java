package grupo11.bcf_store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.service.ProductService;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/clothes")
    public String clothes(Model model) {
        model.addAttribute("products", productService.getProducts());
        return "clothes";
    }

    @PostMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProduct(id);
        if (product != null) {
            productService.removeProduct(product);
            return "redirect:/clothes";
        } else {
            model.addAttribute("errorMessage", "Producto no encontrado.");
            return "error";
        }
    }

    @GetMapping("/add-product")
    public String addProductRedirect(Model model) {
        Product new_product = new Product("", 0, "", null);
        model.addAttribute("product", new_product);
        return "add";
    }

    @GetMapping("/edit-product/{id}")
    public String editProductRedirect(@PathVariable Long id, Model model) {
        Product product_to_edit = productService.getProduct(id);
        model.addAttribute("product", product_to_edit);

        return "add";
    }

    @PostMapping("/add-product")
    public String addProduct(@RequestParam("name") String name,
                             @RequestParam("description") String description,
                             @RequestParam("price") double price,
                             @RequestParam("image") MultipartFile image, Model model) throws Exception{
        productService.submitProductAdded(name, description, price, image);

        if (!name.isEmpty() && !description.isEmpty() && price > 0 && !image.isEmpty()) {
            return "redirect:/clothes";
        } else {
            model.addAttribute("errorMessage", "Error al añadir producto. Por favor, rellene todos los campos.");
            return "error";
        }
    }

    @PostMapping("/edit-product")
    public String editProduct(@RequestParam("id") Long id,
                              @RequestParam("name") String name,
                              @RequestParam("description") String description,
                              @RequestParam("price") double price,
                              @RequestParam("image") MultipartFile image, Model model) throws Exception{
        productService.submitProductEdited(id, name, description, price, image);

        if (!name.isEmpty() && !description.isEmpty() && price > 0) {
            return "redirect:/clothes";
        } else {
            model.addAttribute("errorMessage", "Error al editar producto. Por favor, rellene todos los campos.");
            return "error";
        }
    }

    @GetMapping("/view/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product_to_view = productService.getProduct(id);
        List<Order> productOrders = productService.getProductOrders(id);
        List<Order> uniqueProductOrders = productService.getUniqueProductOrders(id);

        if (product_to_view != null) {
            model.addAttribute("product", product_to_view);
            if (productOrders != null && !productOrders.isEmpty()) {
                model.addAttribute("uniqueProductOrders", uniqueProductOrders);
            }
            return "view";
        } else {
            model.addAttribute("errorMessage", "Producto no encontrado.");
            return "error";
        }
    }
}
