package grupo11.bcf_store.service;

import java.util.List;

import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.Cart;
import grupo11.bcf_store.model.Product;


@Service
public class CartService {
    private final Cart cart = new Cart();

    public List<Product> getProducts() {
        return cart.getProducts();
    }

    public int getTotalItems() {
        return cart.getProducts().size();
    }

    public double getTotalPrice() {
        return cart.getProducts().stream().mapToDouble(Product::getPrice).sum();
    }

    public void addProduct(Product product) {
        if(product != null) {
            cart.addProduct(product);
        }
    }

    public void removeProduct(Product product) {
        if(product != null) {
            cart.removeProduct(product);
        }
    }

    public void clearCart() {
        cart.clearCart();
    }

    public void updateCartProduct(Product updatedProduct) {
        cart.getProducts().replaceAll((product) -> 
            product.getId().equals(updatedProduct.getId()) ? updatedProduct : product
        );
    }
}
