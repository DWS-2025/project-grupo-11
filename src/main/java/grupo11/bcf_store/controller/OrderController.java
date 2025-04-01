package grupo11.bcf_store.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import grupo11.bcf_store.model.CartDTO;
import grupo11.bcf_store.model.OrderDTO;
import grupo11.bcf_store.model.OrderMapper;
import grupo11.bcf_store.model.ProductDTO;
import grupo11.bcf_store.service.CartService;
import grupo11.bcf_store.service.OrderService;

@Controller
public class OrderController {
    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @PostMapping("/create-order/")
    public String createOrder(Model model) {
        CartDTO cart = cartService.getCart(1L);

        if (cart != null) {

            if (cartService.getProductsInCart(cart) != null && !cartService.getProductsInCart(cart).isEmpty()) {
                List<ProductDTO> productsCopy = new ArrayList<>(cartService.getProductsInCart(cart));
                OrderDTO newOrder = orderService.createOrder(productsCopy);
                cartService.clearCart(cart); // Ensure the cart is cleared
                cartService.saveCart(cart);

                return "redirect:/view-order/" + orderMapper.toDomain(newOrder).getId() + "/";
            } else {
                model.addAttribute("errorMessage", "El carrito está vacío.");
                return "error";
            }

        } else {
            model.addAttribute("errorMessage", "Carrito no encontrado.");
            return "error";
        }
    }

    @PostMapping("/delete-order/{id}/")
    public String deleteOrder(@PathVariable Long id, Model model) {
        OrderDTO order_to_delete = orderService.getOrder(id);

        if (order_to_delete != null) {
            orderService.removeOrderFromUser(order_to_delete);
            orderService.remove(id);
            return "redirect:/myaccount/";
        } else {
            model.addAttribute("errorMessage", "Pedido no encontrado.");
            return "error";
        }
    }

    @GetMapping("/view-order/{id}/")
    public String viewOrder(@PathVariable Long id, Model model) {
        OrderDTO order = orderService.getOrder(id);
        if (order != null) {
            model.addAttribute("order", order);
            model.addAttribute("products", orderService.getProductsInOrder(order));
            return "viewOrder";
        } else {
            model.addAttribute("errorMessage", "Pedido no encontrado.");
            return "error";
        }
    }

    @GetMapping("/myaccount/")
    public String myAccount(Model model) {
        model.addAttribute("orders", orderService.getOrders());
        return "myaccount";
    }
}
