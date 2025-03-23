package grupo11.bcf_store.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.Cart;
import grupo11.bcf_store.repository.CartRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    /**
     * Returns always the same cart for simplicity before adding authentication
     * 
     * @return Cart
     */
    public List<Cart> getCarts() {
        return cartRepository.findAll();
    }

    public Cart getCart(Long id) {
        return cartRepository.findById(id).orElse(null);
    }

    public int getTotalItems() {
        return cartRepository.findAll().size();
    }

    public void saveCart(Cart cart) {
        if (cart != null) {
            cartRepository.save(cart);
        }
    }

    public void removeCart(Cart cart) {
        if (cart != null && cart.getTotalItems() > 0) {
            cartRepository.delete(cart);
        }
    }

    public void clearCart() {
        cartRepository.deleteAll();
    }

    public void clearCart(Cart cart) {
        if (cart != null) {
            cart.getProducts().clear();
            cartRepository.save(cart);
        }
    }

}
