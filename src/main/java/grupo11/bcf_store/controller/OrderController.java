package grupo11.bcf_store.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import grupo11.bcf_store.model.Cart;
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
        Cart cart = cartService.getCart(1L);

        if (cart != null) {

            if(cart.getProducts() != null && !cart.getProducts().isEmpty()) {
                List<Product> productsCopy = new ArrayList<>(cart.getProducts());
                Order newOrder = orderService.createOrder(productsCopy);
                cart.clearCart();
                cartService.saveCart(cart);

                return "redirect:/view-order/" + newOrder.getId();
            } else {
                model.addAttribute("errorMessage", "El carrito está vacío.");
                return "error";
            }

        } else {
            model.addAttribute("errorMessage", "Carrito no encontrado.");
            return "error";
        }
    }

    @PostMapping("/delete-order/{id}")
    public String deleteOrder(@PathVariable Long id, Model model) {
        Order order_to_delete = orderService.getOrder(id);

        if (order_to_delete != null) {
            orderService.remove(id);
            return "redirect:/myaccount";
        } else {
            model.addAttribute("errorMessage", "Pedido no encontrado.");
            return "error";
        }
    }

    @GetMapping("/view-order/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Order order = orderService.getOrder(id);
        if (order != null) {
            model.addAttribute("order", order);
            model.addAttribute("products", order.getProducts());
            return "viewOrder";
        } else {
            model.addAttribute("errorMessage", "Pedido no encontrado.");
            return "error";
        }
    }

    @GetMapping("/myaccount")
    public String myAccount(Model model) {
        model.addAttribute("orders", orderService.getOrders());
        return "myaccount";
    }
}
