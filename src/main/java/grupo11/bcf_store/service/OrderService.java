package grupo11.bcf_store.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.repository.OrderRepository;

@Service
public class OrderService {
        
    @Autowired
    private OrderRepository orderRepository;

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public void remove(Long id) {
        orderRepository.deleteById(id);
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Order createOrder(List<Product> products) {
        Order newOrder = new Order(products);

        for (Product product : products) {
            product.getOrders().add(newOrder);
        }

        orderRepository.save(newOrder);
        return newOrder;
    }

}
