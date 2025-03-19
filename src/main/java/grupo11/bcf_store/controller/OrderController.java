package grupo11.bcf_store.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import grupo11.bcf_store.model.Cart;
import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.repository.CartRepository;
import grupo11.bcf_store.repository.OrderRepository;


@Controller
public class OrderController {
    @Autowired
	private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/create-order")
    public String createOrder(Long cartId, Model model) {
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            if (cart.getProducts() != null && !cart.getProducts().isEmpty()) {
                List<Product> productsCopy = new ArrayList<>(cart.getProducts());
                Order newOrder = new Order(productsCopy);
                orderRepository.save(newOrder);
                cart.clearCart(); // Vacía el carrito sin afectar el pedido
                cartRepository.save(cart); // Guarda el carrito actualizado en la base de datos
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
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            orderRepository.delete(orderOptional.get());
            return "redirect:/myaccount";
        } else {
            model.addAttribute("errorMessage", "Pedido no encontrado.");
            return "error";
        }
    }

    @GetMapping("/view-order/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
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
        model.addAttribute("orders", orderRepository.findAll());
        return "myaccount";
    }
}
