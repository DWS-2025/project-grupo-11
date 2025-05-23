package grupo11.bcf_store.controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import grupo11.bcf_store.model.dto.CartDTO;
import grupo11.bcf_store.model.dto.OrderDTO;
import grupo11.bcf_store.model.dto.ProductDTO;
import grupo11.bcf_store.service.CartService;
import grupo11.bcf_store.service.OrderService;
import grupo11.bcf_store.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OrderWebController {
    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/orders/")
    public String order(Model model, HttpServletRequest request) {
        String username = userService.getLoggedInUsername(request);
        if (username == null) {
            model.addAttribute("errorMessage", "Usuario no autenticado.");
            return "error";
        }

        if(userService.isAdmin(request)) {
            model.addAttribute("orders", orderService.getOrders());
            return "orders";
        } else {
            model.addAttribute("orders", orderService.getOrdersByUsername(username)); // Filter orders by user
            return "orders";
        }
    }

    @PostMapping("/create-order/")
    public String createOrder(Model model, HttpServletRequest request) {
        String username = userService.getLoggedInUsername(request);
        if (username == null) {
            model.addAttribute("errorMessage", "Usuario no autenticado.");
            return "error";
        }

        long cartId = userService.getCartIdByUsername(username); // Get cart ID for the user
        if (!cartService.canAccessCart(request, cartId)) {
            model.addAttribute("errorMessage", "No autorizado.");
            return "error";
        }
        CartDTO cart = cartService.getCart(cartId);

        if (cart != null) {
            List<ProductDTO> productsInCart = cartService.getProductsInCart(cart);
            if (productsInCart != null && !productsInCart.isEmpty()) {
                OrderDTO newOrder = orderService.createOrderForUser(productsInCart, request); // Associate order with user
                cartService.clearCart(cart);
                return "redirect:/view-order/" + newOrder.id() + "/";
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
    public String deleteOrder(@PathVariable long id, Model model, HttpServletRequest request) {
        if (!orderService.canAccessOrder(request, id)) {
            model.addAttribute("errorMessage", "No autorizado.");
            return "error";
        }
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
    public String viewOrder(@PathVariable long id, Model model, HttpServletRequest request) {
        String username = userService.getLoggedInUsername(request);
        if (username == null) {
            model.addAttribute("errorMessage", "Usuario no autenticado.");
            return "error";
        }

        if (orderService.canAccessOrder(request, id)) {
            OrderDTO order = orderService.getOrder(id);

            String orderOwnerUsername = null;
            if (order.userId() != 0) {
                var user = userService.findById(order.userId());
                if (user != null) {
                    orderOwnerUsername = user.getUsername();
                }
            }

            model.addAttribute("order", order);
            model.addAttribute("products", orderService.getProductsInOrder(order));
            model.addAttribute("username", orderOwnerUsername != null ? orderOwnerUsername : "");
            return "viewOrder";
        } else {
            model.addAttribute("errorMessage", "No tienes permiso para acceder a este pedido.");
            return "error";
        }
    }
}
