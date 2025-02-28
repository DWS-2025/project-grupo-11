package grupo11.bcf_store.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.service.CartService;
import grupo11.bcf_store.service.OrderService;


@Controller
public class OrderController {
    @Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;    

    @PostMapping("/create-order")
    public String createOrder(Model model) {
        if (cartService.getProducts() != null && !cartService.getProducts().isEmpty()) {
            List<Product> productsCopy = new ArrayList<>(cartService.getProducts());
            String id = orderService.createOrder(productsCopy);
            cartService.clearCart();
            return "redirect:/view-order/" + id;
        } else {
            model.addAttribute("errorMessage", "Pedido no encontrado o vacío.");
            return "error";
        }
    }

    @PostMapping("/delete-order/{id}")
    public String deleteOrder(@PathVariable String id, Model model) {
        orderService.remove(id);
        return "redirect:/myaccount";
    }

    @GetMapping("/view-order/{id}")
    public String viewOrder(@PathVariable String id, Model model) {
        Order order = orderService.get(id);

        if (order.getProducts() != null && order.getProducts().size() > 0) {
            model.addAttribute("order", order);
            model.addAttribute("products", order.getProducts());
            return "viewOrder";
        } else {
            model.addAttribute("errorMessage", "Pedido no encontrado o vacío.");
            return "error";
        }
    }

    @GetMapping("/myaccount")
    public String myAccount(Model model) {
        model.addAttribute("orders", orderService.getOrders().values());
        return "myaccount";
    }
}
