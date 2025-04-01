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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.service.ProductService;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/clothes/")
    public String getPosts(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;

        if (page < 0) {
            page = 0;
        }

        Page<Product> productPage = productService.getProducts(PageRequest.of(page, pageSize));

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("prev", page > 0 ? page - 1 : 0);
        model.addAttribute("next", page < productPage.getTotalPages() - 1 ? page + 1 : page);
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", page < productPage.getTotalPages() - 1);

        return "clothes";
    }

    @PostMapping("/delete-product/{id}/")
    public String deleteProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProduct(id);
        if (product != null) {
            productService.removeProduct(product);
            return "redirect:/clothes/";
        } else {
            model.addAttribute("errorMessage", "Producto no encontrado.");
            return "error";
        }
    }

    @GetMapping("/add-product/")
    public String addProductRedirect(Model model) {
        Product new_product = new Product("", 0, "", null);
        model.addAttribute("product", new_product);
        return "add";
    }

    @GetMapping("/edit-product/{id}/")
    public String editProductRedirect(@PathVariable Long id, Model model) {
        Product product_to_edit = productService.getProduct(id);
        model.addAttribute("product", product_to_edit);

        return "add";
    }

    @PostMapping("/add-product/")
    public String addProduct(@RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("image") MultipartFile image, Model model) throws Exception {
        productService.submitProductAdded(name, description, price, image);

        if (!name.isEmpty() && !description.isEmpty() && price > 0 && !image.isEmpty()) {
            return "redirect:/clothes/";
        } else {
            model.addAttribute("errorMessage", "Error al añadir producto. Por favor, rellene todos los campos.");
            return "error";
        }
    }

    @PostMapping("/edit-product/")
    public String editProduct(@RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("image") MultipartFile image, Model model) throws Exception {
        productService.submitProductEdited(id, name, description, price, image);

        if (!name.isEmpty() && !description.isEmpty() && price > 0) {
            return "redirect:/clothes/";
        } else {
            model.addAttribute("errorMessage", "Error al editar producto. Por favor, rellene todos los campos.");
            return "error";
        }
    }

    @GetMapping("/view/{id}/")
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

    @GetMapping("/search-products/")
    public String showProducts(Model model,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double price) {
        List<Product> products;

        if (name != null && !name.isEmpty() && description != null && !description.isEmpty() && price != null) {
            products = productService.findByNameAndDescriptionAndPrice(name, description, price);
        } else if (name != null && !name.isEmpty() && description != null && !description.isEmpty()) {
            products = productService.findByNameAndDescription(name, description);
        } else if (name != null && !name.isEmpty() && price != null) {
            products = productService.findByNameAndPrice(name, price);
        } else if (description != null && !description.isEmpty() && price != null) {
            products = productService.findByDescriptionAndPrice(description, price);
        } else if (name != null && !name.isEmpty()) {
            products = productService.findByName(name);
        } else if (description != null && !description.isEmpty()) {
            products = productService.findByDescription(description);
        } else if (price != null) {
            products = productService.findByPrice(price);
        } else {
            products = productService.getProducts();
        }

        model.addAttribute("products", products);
        return "clothes";
    }

}
