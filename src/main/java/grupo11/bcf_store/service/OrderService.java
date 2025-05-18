package grupo11.bcf_store.service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.Order;
import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.User;
import grupo11.bcf_store.model.dto.OrderDTO;
import grupo11.bcf_store.model.dto.ProductDTO;
import grupo11.bcf_store.model.dto.ProductSimpleDTO;
import grupo11.bcf_store.model.mapper.OrderMapper;
import grupo11.bcf_store.model.mapper.ProductMapper;
import grupo11.bcf_store.repository.OrderRepository;
import grupo11.bcf_store.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class OrderService {
        
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    public OrderDTO save(OrderDTO orderDTO) {
        Order order = orderMapper.toDomain(orderDTO);
        return orderMapper.toDTO(orderRepository.save(order));
    }

    public void assignOrderToUser(OrderDTO orderDTO, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + principal.getName()));
        Order order = orderMapper.toDomain(orderDTO);
        order.setUser(user);
        orderRepository.save(order);
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

    public List<OrderDTO> getOrdersByUsername(String username) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getUser().getUsername().equals(username)) // Filter by username
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO createOrder(List<ProductDTO> productsDTO, HttpServletRequest request) {
        List<Product> products = productMapper.toDomain(productsDTO);

        for (Product product : products) {
            if (product.getId() == null) {
                throw new IllegalStateException("El producto no está guardado en la base de datos: " + product.getName());
            }
        }

        Order newOrder = new Order(products);
        newOrder.setUser(userRepository.findByUsername(request.getUserPrincipal().getName()).orElseThrow(() -> 
            new IllegalStateException("Usuario no encontrado: " + request.getUserPrincipal().getName())));

        for (Product product : products) {
            product.getOrders().add(newOrder);
        }

        newOrder = orderRepository.save(newOrder);

        return orderMapper.toDTO(newOrder);
    }

    public OrderDTO createOrderForUser(List<ProductDTO> productsDTO, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> 
            new IllegalStateException("Usuario no encontrado: " + username));
        
        List<Product> products = productsDTO.stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());

        for (Product product : products) {
            if (product.getId() == null) {
                throw new IllegalStateException("El producto no está guardado en la base de datos: " + product.getName());
            }
        }

        Order newOrder = new Order();
        newOrder.setUser(user); // Associate the order with the user
        newOrder.setProducts(products); //Associate the order with the products

        // Associate the products with the order
        for (Product product : products) {
            product.getOrders().add(newOrder);
        }

        newOrder = orderRepository.save(newOrder); // Save the order to the database

        return orderMapper.toDTO(newOrder);
    }

    public OrderDTO createOrderFromDTO(OrderDTO orderDTO, Principal principal) {
        Order order = orderMapper.toDomain(orderDTO);
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + principal.getName()));
            order.setUser(user);
        }
        // Asociar productos con el pedido (bidireccional)
        for (Product product : order.getProducts()) {
            product.getOrders().add(order);
        }
        order = orderRepository.save(order);
        return orderMapper.toDTO(order);
    }

    public OrderDTO restCreateOrderFromDTO(OrderDTO orderDTO, Principal principal) {
        // Recover the full product information for each product in the orderDTO
        List<ProductSimpleDTO> fullProducts = orderDTO.products().stream()
            .map(p -> {
                var product = productService.getProduct(p.id());
                if (product != null) {
                    return new ProductSimpleDTO(
                        product.id(),
                        product.name(),
                        product.price(),
                        product.description(),
                        product.image()
                    );
                } else {
                    return p;
                }
            })
            .toList();

        OrderDTO fixedOrderDTO = new OrderDTO(
            orderDTO.id(),
            fullProducts,
            orderDTO.userId()
        );

        return createOrderFromDTO(fixedOrderDTO, principal);
    }

    public void removeOrderFromUser(OrderDTO orderDTO) {
        Order order = orderMapper.toDomain(orderDTO);
        order.getProducts().forEach(product -> product.getOrders().remove(order));
        orderRepository.delete(order);
    }

    public boolean isOrderOwnedByUser(long orderId, String username) {
        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getUsername().equals(username))
                .orElse(false);
    }

    public boolean isSelf(HttpServletRequest request, long userId) {
        return userService.isSelf(request, userId);
    }

    public boolean isAdmin(HttpServletRequest request) {
        return userService.isAdmin(request);
    }

    public boolean canAccessOrder(HttpServletRequest request, long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;
        String username = userService.getLoggedInUsername(request);
        if (username == null) return false;
        return (order.getUser() != null && order.getUser().getUsername().equals(username)) || isAdmin(request);
    }

    // DTO methods
    public OrderDTO toDTO (Order order) {
        return orderMapper.toDTO(order);
    }

    public Order toDomain (OrderDTO orderDTO) {
        return orderMapper.toDomain(orderDTO);
    }
}
