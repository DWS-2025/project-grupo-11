package grupo11.bcf_store.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.OrderDTO;
import grupo11.bcf_store.model.OrderMapper;
import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.ProductDTO;
import grupo11.bcf_store.model.ProductMapper;
import grupo11.bcf_store.repository.OrderRepository;

@Service
public class OrderService {
        
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    public OrderDTO save(OrderDTO orderDTO) {
        Order order = orderMapper.toDomain(orderDTO);
        return orderMapper.toDTO(orderRepository.save(order));
    }

    public OrderDTO getOrder(long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDTO)
                .orElse(null);
    }

    public void remove(long id) {
        orderRepository.deleteById(id);
    }

    public List<OrderDTO> getOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsInOrder(OrderDTO orderDTO) {
        Order order = orderMapper.toDomain(orderDTO);
        return productMapper.toDTOs(order.getProducts());
    }

    public List<OrderDTO> getOrdersByUserId(long userId) {
        return orderRepository.findById(userId)
                .stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO createOrder(List<ProductDTO> productsDTO) {
        List<Product> products = productMapper.toDomain(productsDTO);

        for (Product product : products) {
            if (product.getId() == null) {
                throw new IllegalStateException("El producto no está guardado en la base de datos: " + product.getName());
            }
        }

        Order newOrder = new Order(products);

        for (Product product : products) {
            product.getOrders().add(newOrder);
        }

        newOrder = orderRepository.save(newOrder);

        return orderMapper.toDTO(newOrder);
    }

    public void removeOrderFromUser(OrderDTO orderDTO) {
        Order order = orderMapper.toDomain(orderDTO);
        order.getProducts().forEach(product -> product.getOrders().remove(order));
        orderRepository.delete(order);
    }
}
