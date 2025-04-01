package grupo11.bcf_store.controller;

import java.net.URI;
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

import grupo11.bcf_store.model.OrderMapper;
import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.OrderDTO;
import grupo11.bcf_store.repository.OrderRepository;

@RestController
@RequestMapping("/api/orders/")
public class OrderRestController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper mapper;

    // API methods
    @GetMapping("/")
    public List<OrderDTO> getOrders() {
        return toDTOs(orderRepository.findAll());
    }

    @GetMapping("/{id}/")
    public OrderDTO getOrder(@PathVariable long id) {
        return toDTO(orderRepository.findById(id).orElseThrow());
    }

    @PostMapping("/")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        Order order = toDomain(orderDTO);

        orderRepository.save(order);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(order.getId()).toUri();

        return ResponseEntity.created(location).body(toDTO(order));
    }

    @PutMapping("/{id}/")
    public OrderDTO replaceOrder(@PathVariable long id, @RequestBody OrderDTO updatedOrderDTO) {
        if (orderRepository.existsById(id)) {
            Order updatedOrder = toDomain(updatedOrderDTO);

            updatedOrder.setId(id);
            orderRepository.save(updatedOrder);

            return toDTO(updatedOrder);
        } else {
            throw new NoSuchElementException();
        }
    }

    @DeleteMapping("/{id}/")
    public OrderDTO deleteOrder(@PathVariable long id) {
        Order order = orderRepository.findById(id).orElseThrow();

        orderRepository.deleteById(id);

        return toDTO(order);
    }

    // DTO methods
    private OrderDTO toDTO(Order order) {
        return mapper.toDTO(order);
    }

    private Order toDomain(OrderDTO orderDTO) {
        return mapper.toDomain(orderDTO);
    }

    private List<OrderDTO> toDTOs(List<Order> orders) {
        return mapper.toDTOs(orders);
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
