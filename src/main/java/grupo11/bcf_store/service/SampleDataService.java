package grupo11.bcf_store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.User;
import grupo11.bcf_store.repository.ProductRepository;
import grupo11.bcf_store.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class SampleDataService {

    private final ProductService productService;
    
    @Autowired 
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    SampleDataService(ProductService productService) {
        this.productService = productService;
    }

    @PostConstruct
    public void init() throws Exception {
        // Default user
        userRepository.save(new User("m.serranog.2023@alumnos.urjc.es", "hola"));

        // Default products
        productRepository.save(new Product("1ª EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido.", productService.getImageBlob("/static/images/shirt0.jpg")));
        productRepository.save(new Product("3º EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido.", productService.getImageBlob("/static/images/shirt1.jpg")));
        productRepository.save(new Product("CHUBASQUERO NEGRO 23/24", 60, "Chubasquero del Burgos CF.", productService.getImageBlob("/static/images/chubasquero.jpg")));
        productRepository.save(new Product("PARKA BURGOS CF 23/24", 60, "Parka con el escudo del equipo.", productService.getImageBlob("/static/images/parka.jpg")));
        productRepository.save(new Product("BUFANDA BURGOS CF", 20, "Bufanda con el nombre y escudo del equipo", productService.getImageBlob("/static/images/scarf.jpg")));
        productRepository.save(new Product("BALON BLANQUINEGRO", 20, "Disfruta del balón de tu equipo", productService.getImageBlob("/static/images/balon.jpg")));
        productRepository.save(new Product("BABERO BLANQUINEGRO", 20, "Babero con escudo del equipo", productService.getImageBlob("/static/images/babero.jpg")));
    }
}
