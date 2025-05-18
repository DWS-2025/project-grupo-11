package grupo11.bcf_store.controller.rest;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import grupo11.bcf_store.model.dto.OrderDTO;
import grupo11.bcf_store.service.OrderService;
import grupo11.bcf_store.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/orders/")
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    // API methods
    @GetMapping("/")
    public List<OrderDTO> getOrders(HttpServletRequest request) {
        String username = userService.getLoggedInUsername(request);
        
        if (username == null) {
            throw new SecurityException("Usuario no autenticado.");
        }

        if(userService.isAdmin(request)) {
            return orderService.getOrders();
        } else {
            return orderService.getOrdersByUsername(username);
        }
    }

    @GetMapping("/{id}/")
    public OrderDTO getOrder(@PathVariable long id, HttpServletRequest request) {
        if(userService.isAdmin(request)) {
            return orderService.getOrder(id);
        } else {
            if(orderService.canAccessOrder(request, id)) {
                return orderService.getOrder(id);
            } else {
                throw new SecurityException("No autorizado");
            }
        }
    }

    @PostMapping("/")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO, Principal principal) {
        OrderDTO savedOrder = orderService.restCreateOrderFromDTO(orderDTO, principal);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedOrder.id()).toUri();
        return ResponseEntity.created(location).body(savedOrder);
    }

    @PutMapping("/{id}/")
    public OrderDTO replaceOrder(@PathVariable long id, @RequestBody OrderDTO updatedOrderDTO, jakarta.servlet.http.HttpServletRequest request) {
        if (!orderService.canAccessOrder(request, id)) {
            throw new SecurityException("No autorizado");
        }
        // Save will update if id exists
        return orderService.save(updatedOrderDTO);
    }

    @Transactional
    @DeleteMapping("/{id}/")
    public OrderDTO deleteOrder(@PathVariable long id, HttpServletRequest request) {
        if (!orderService.canAccessOrder(request, id)) {
            throw new SecurityException("No autorizado");
        }
        OrderDTO order = orderService.getOrder(id);
        if (order != null) {
            orderService.remove(id);
            return order;
        } else {
            throw new NoSuchElementException();
        }
    }

    // Exception handling
    @ControllerAdvice
    class NoSuchElementExceptionControllerAdvice {
        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(NoSuchElementException.class)
        public void handleNoTFound() {
        }
    }
}
