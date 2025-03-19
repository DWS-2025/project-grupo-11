package grupo11.bcf_store.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.Product;

@Service
public class OrderService {
    private final Map<String, Order> orders = new HashMap<>();
    private final AtomicInteger orderIdCounter;

    public OrderService() {
        int maxIdOrder = orders.keySet().stream().mapToInt(Integer::parseInt).max().orElse(0);
        this.orderIdCounter = new AtomicInteger(maxIdOrder + 1);
    }

    public Order get(String id) {
        return orders.get(id);
    }

    public void remove(String id) {
        orders.remove(id);
    }

    public int getAndIncrement() {
        return orderIdCounter.getAndIncrement();
    }

    public void put(String id, Order order) {
        orders.put(id, order);
    }

    public Map<String, Order> getOrders() {
        return orders;
    }

    public String createOrder(List<Product> products) {
        String id = String.valueOf(this.getAndIncrement());
        Order newOrder = new Order(products);
        this.put(id, newOrder);
        for (Product product : products) {
            product.getOrders().add(newOrder);
        }
        return id;
    }
}
