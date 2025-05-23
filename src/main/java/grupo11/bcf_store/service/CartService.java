package grupo11.bcf_store.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.Cart;
import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.dto.CartDTO;
import grupo11.bcf_store.model.dto.ProductDTO;
import grupo11.bcf_store.model.mapper.CartMapper;
import grupo11.bcf_store.model.mapper.ProductMapper;
import grupo11.bcf_store.repository.CartRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserService userService;

    public List<CartDTO> getCarts() {
        return cartMapper.toDTOs(cartRepository.findAll());
    }

    public CartDTO getCart(long id) {
        Cart cart = cartRepository.findById(id).orElse(null);
        return cart != null ? cartMapper.toDTO(cart) : null;
    }

    public List<CartDTO> getCartByUsername(String username) {
        return cartRepository.findAll().stream()
                .filter(cart -> cart.getUser().getUsername().equals(username))
                .map(cartMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CartDTO cartBelongsToUser(long cartId, String username) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null && cart.getUser().getUsername().equals(username)) {
            return cartMapper.toDTO(cart);
        }
        return null;
    }

    public int getTotalItems() {
        return cartRepository.findAll().size();
    }

    public Cart saveCart(CartDTO cartDTO) {
        if (cartDTO != null) {
            Cart cart = cartMapper.toDomain(cartDTO);
            cartRepository.save(cart);
            return cart;
        }

        return null;
    }

    public CartDTO updateCartbyId(long id, CartDTO updatedCartDTO) {  
        if (cartRepository.existsById(id)) {
            Cart updatedCart = cartMapper.toDomain(updatedCartDTO);

            updatedCart.setId(id);
            cartRepository.save(updatedCart);

            return cartMapper.toDTO(updatedCart);
        } else {
            throw new NoSuchElementException();
        }
    }

    public void removeCart(CartDTO cartDTO) {
        if (cartDTO != null) {
            Cart cart = cartMapper.toDomain(cartDTO);
            if (cart.getTotalItems() > 0) {
                cartRepository.delete(cart);
            }
        }
    }

    public void clearCart(CartDTO cartDTO) {
        if (cartDTO != null) {
            Cart cart = cartMapper.toDomain(cartDTO);
            cart.getProducts().clear();
            cartRepository.save(cart);
        }
    }

    public List<ProductDTO> getProductsInCart(long cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null) {
            List<Product> products = cart.getProducts();
            if (products != null) {
                return productMapper.toDTOs(products);
            }
        }
        return null;
    }

    public List<ProductDTO> getProductsInCart(CartDTO cartDTO) {
        if (cartDTO != null) {
            Cart cart = cartMapper.toDomain(cartDTO);
            List<Product> products = cart.getProducts();
            if (products != null) {
                return productMapper.toDTOs(products);
            }
        }
        return null;
    }

    public double getTotalPrice(long cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null) {
            return cart.getTotalPrice();
        }
        return 0;
    }

    public int getTotalItemsInCart(long cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null) {
            return cart.getTotalItems();
        }
        return 0;
    }

    public void addProductToCart(long cartId, ProductDTO productDTO) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null && productDTO != null) {
            Product product = productMapper.toDomain(productDTO);
            cart.addProduct(product);
            cartRepository.save(cart);
        }
    }

    public void removeProductFromCart(long cartId, ProductDTO productDTO) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null && productDTO != null) {
            Product productToRemove = cart.getProducts().stream()
                .filter(product -> product.getId() == productDTO.id())
                .findFirst()
                .orElse(null);
            if (productToRemove != null) {
                cart.getProducts().remove(productToRemove);
            }
            cartRepository.save(cart);
        }
    }

    public void deleteCartById(long id) {
        Cart cart = cartRepository.findById(id).orElse(null);
        if (cart != null) {
            cart.getProducts().clear();
            cartRepository.save(cart);
            cart.getUser().deleteCart();
            cartRepository.deleteById(id);
        }
    }

    public boolean isSelf(HttpServletRequest request, long userId) {
        return userService.isSelf(request, userId);
    }

    public boolean isAdmin(HttpServletRequest request) {
        return userService.isAdmin(request);
    }

    public boolean canAccessCart(HttpServletRequest request, long cartId) {
        CartDTO cart = getCart(cartId);
        if (cart == null) return false;
        String username = userService.getLoggedInUsername(request);
        if (username == null) return false;
        return (cart.userId() == userService.getLoggedInUserId(request)) || isAdmin(request);
    }

}
