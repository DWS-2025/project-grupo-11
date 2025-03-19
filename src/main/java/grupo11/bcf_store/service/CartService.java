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

    public List<Cart> getCarts() {
        return cartRepository.findAll();
    }

    public int getTotalItems() {
        return cartRepository.findAll().size();
    }

    public void addCart(Cart cart) {
        if(cart != null && cart.getTotalItems() > 0) {
            cartRepository.save(cart);
        }
    }

    public void removeCart(Cart cart) {
        if(cart != null && cart.getTotalItems() > 0) {
            cartRepository.delete(cart);
        }
    }

    public void clearCart() {
        cartRepository.deleteAll();
    }

}
