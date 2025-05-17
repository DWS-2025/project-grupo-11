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
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/orders/")
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    // API methods
    @GetMapping("/")
    public List<OrderDTO> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{id}/")
    public OrderDTO getOrder(@PathVariable long id) {
        return orderService.getOrder(id);
    }

    @PostMapping("/")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO savedOrder = orderService.save(orderDTO);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedOrder.id()).toUri();
        return ResponseEntity.created(location).body(savedOrder);
    }

    @PutMapping("/{id}/")
    public OrderDTO replaceOrder(@PathVariable long id, @RequestBody OrderDTO updatedOrderDTO) {
        // Save will update if id exists
        return orderService.save(updatedOrderDTO);
    }

    @Transactional
    @DeleteMapping("/{id}/")
    public OrderDTO deleteOrder(@PathVariable long id) {
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
