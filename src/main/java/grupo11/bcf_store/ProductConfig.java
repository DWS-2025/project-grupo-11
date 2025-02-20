package grupo11.bcf_store;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProductConfig {

    @Bean
    public Map<String, Product> products() {
        Map<String, Product> products = new HashMap<>();
        products.put("main_kit", new Product("Main", 60, 100, "Camiseta oficial de partido", "http://example.com/laptop.jpg"));
        products.put("second_kit", new Product("Second", 60, 100, "Camiseta oficial de partido", "http://example.com/smartphone.jpg"));
        products.put("flag", new Product("Flag", 20, 100, "Bandera con el escudo del equipo", "http://example.com/laptop.jpg"));
        products.put("skirt", new Product("Skirt", 20, 100, "Bufanda con el nombre y escudo del equipo", "http://example.com/smartphone.jpg"));
        products.put("ball", new Product("Ball", 20, 100, "Disfruta del balón de tu equipo", "http://example.com/smartphone.jpg"));
        products.put("babero", new Product("Main", 20, 100, "Babero con escudo del equipo", "http://example.com/laptop.jpg"));
        return products;
    }
}

