package grupo11.bcf_store.controller.web;

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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.dto.OrderDTO;
import grupo11.bcf_store.model.dto.ProductDTO;
import grupo11.bcf_store.service.ProductService;
import grupo11.bcf_store.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ProductWebController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/clothes/")
    public String getProducts(Model model, HttpServletRequest request, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;

        Page<ProductDTO> productPage = productService.getProducts(PageRequest.of(page, pageSize));

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        
        model.addAttribute("username", userService.getLoggedInUsername(request));
        model.addAttribute("admin", userService.isAdmin(request));

        return "clothes";
    }

    @GetMapping("/clothes/more/")
    @ResponseBody
    public List<ProductDTO> getMoreProducts(@RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;
        Page<ProductDTO> productPage = productService.getProducts(PageRequest.of(page, pageSize));
        return productPage.getContent();
    }

    @PostMapping("/delete-product/{id}/")
    public String deleteProduct(@PathVariable long id, Model model) {
        ProductDTO product = productService.getProduct(id);
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
    public String editProductRedirect(@PathVariable long id, Model model) {
        ProductDTO product_to_edit = productService.getProduct(id);
        model.addAttribute("product", product_to_edit);

        return "add";
    }

    @PostMapping("/add-product/")
    public String addProduct(@RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("image") MultipartFile image, Model model) throws Exception {
        if (image.isEmpty()) {
            model.addAttribute("errorMessage", "Debe subir una imagen.");
            return "error";
        }
        String mimeType = image.getContentType();
        if (mimeType == null || !(mimeType.equals("image/png") || mimeType.equals("image/jpg") || mimeType.equals("image/jpeg") || mimeType.equals("image/webp"))) {
            model.addAttribute("errorMessage", "Solo se permiten imágenes");
            return "error";
        }

        productService.submitProductAdded(name, description, price, image);

        if (!name.isEmpty() && !description.isEmpty() && price > 0 && !image.isEmpty()) {
            return "redirect:/clothes/";
        } else {
            model.addAttribute("errorMessage", "Error al añadir producto. Por favor, rellene todos los campos.");
            return "error";
        }
    }

    @PostMapping("/edit-product/")
    public String editProduct(@RequestParam("id") long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("image") MultipartFile image, Model model) throws Exception {
        if (!image.isEmpty()) {
            String mimeType = image.getContentType();
            if (mimeType == null || !(mimeType.equals("image/png") || mimeType.equals("image/jpg") || mimeType.equals("image/jpeg") || mimeType.equals("image/webp"))) {
                model.addAttribute("errorMessage", "Solo se permiten imágenes.");
                return "error";
            }
        }

        productService.submitProductEdited(id, name, description, price, image);

        if (!name.isEmpty() && !description.isEmpty() && price > 0) {
            return "redirect:/clothes/";
        } else {
            model.addAttribute("errorMessage", "Error al editar producto. Por favor, rellene todos los campos.");
            return "error";
        }
    }

    @GetMapping("/view/{id}/")
    public String viewProduct(@PathVariable long id, Model model, HttpServletRequest request) {
        ProductDTO product_to_view = productService.getProduct(id);
        List<OrderDTO> productOrders = productService.getProductOrders(id);
        List<OrderDTO> uniqueProductOrders = productService.getUniqueProductOrders(id);
        List<OrderDTO> userUniqueProductOrders = productService.getUserUniqueProductOrders(id, userService.getLoggedInUserId(request));
        boolean isAdmin = userService.isAdmin(request);
        
        model.addAttribute("username", userService.getLoggedInUsername(request));
        model.addAttribute("admin", userService.isAdmin(request));

        if (product_to_view != null) {
            model.addAttribute("product", product_to_view);

            if (productOrders != null && !productOrders.isEmpty()) {
                if(isAdmin) {
                    if(uniqueProductOrders != null && !uniqueProductOrders.isEmpty()) {
                        model.addAttribute("uniqueProductOrders", uniqueProductOrders);
                    } else {
                        model.addAttribute("uniqueProductOrders", null);
                    }
                } else {
                    if(userUniqueProductOrders != null && !userUniqueProductOrders.isEmpty()) {
                        model.addAttribute("uniqueProductOrders", userUniqueProductOrders);
                    } else {
                        model.addAttribute("uniqueProductOrders", null);
                    }
                }
                
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
            @RequestParam(required = false) Double price,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productPage = productService.searchProducts(name, description, price, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);

        return "clothes";
    }

    @GetMapping("/search-products/more/")
    @ResponseBody
    public List<ProductDTO> getMoreSearchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double price,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return productService.searchProducts(name, description, price, pageable).getContent();
    }
    

    @GetMapping("/product-image/{id}/")
    public ResponseEntity<byte[]> getProductImage(@PathVariable long id) throws Exception {
        byte[] image = productService.getProductImage(id);
        if (image != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(image);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
