package grupo11.bcf_store.service;

import java.util.List;

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

    public List<CartDTO> getCarts() {
        return cartMapper.toDTOs(cartRepository.findAll());
    }

    public CartDTO getCart(long id) {
        Cart cart = cartRepository.findById(id).orElse(null);
        return cart != null ? cartMapper.toDTO(cart) : null;
    }

    public int getTotalItems() {
        return cartRepository.findAll().size();
    }

    public void saveCart(CartDTO cartDTO) {
        if (cartDTO != null) {
            Cart cart = cartMapper.toDomain(cartDTO);
            cartRepository.save(cart);
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
            cartRepository.deleteById(id);
        }
    }

}
