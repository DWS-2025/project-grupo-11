package grupo11.bcf_store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.Product;
import grupo11.bcf_store.model.User;
import grupo11.bcf_store.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class SampleDataService {
    
    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() throws Exception {
        // Default user
        userRepository.save(new User("m.serranog.2023@alumnos.urjc.es", "hola"));

        // Default products
        productService.save(new Product("1ª EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido", productService.getImageBlob("/static/images/shirt0.jpg")));
        productService.save(new Product("2ª EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido", productService.getImageBlob("/static/images/shirt1.jpg")));
        productService.save(new Product("3º EQUIPACION BURGOS CF 24/25", 60, "Camiseta oficial de partido", productService.getImageBlob("/static/images/shirt2.jpg")));
        productService.save(new Product("CAMISETA BCF LOVE", 20, "Regalo perfecto para San Valentín", productService.getImageBlob("/static/images/shirtL.jpg")));
        productService.save(new Product("CAMISETA BCF VIDA", 20, "Demuestra tu afición por el BCF", productService.getImageBlob("/static/images/shirtV.jpg")));
        productService.save(new Product("CHUBASQUERO NEGRO 23/24", 60, "Chubasquero del Burgos CF", productService.getImageBlob("/static/images/chubasquero.jpg")));
        productService.save(new Product("PARKA BURGOS CF 23/24", 60, "Parka con el escudo del equipo", productService.getImageBlob("/static/images/parka.jpg")));
        productService.save(new Product("PANTALON BURGOS CF 22/23", 30, "Pantalón con el escudo del equipo", productService.getImageBlob("/static/images/joggers.jpg")));
        productService.save(new Product("BUFANDA BURGOS CF", 20, "Bufanda con el nombre y escudo del equipo", productService.getImageBlob("/static/images/scarf.jpg")));
        productService.save(new Product("BALON BLANQUINEGRO", 20, "Disfruta del balón de tu equipo", productService.getImageBlob("/static/images/balon.jpg")));
        productService.save(new Product("CALCETINES BCF", 10, "Calcetines oficiales del BCF", productService.getImageBlob("/static/images/socks.jpg")));
        productService.save(new Product("ABRIDOR BCF", 5, "Abridor con imán del club", productService.getImageBlob("/static/images/opener.jpg")));
        productService.save(new Product("BANDERIN BCF", 10, "Banderin para colocar en coche", productService.getImageBlob("/static/images/pennant.jpg")));
        productService.save(new Product("BOLSA BCF", 10, "Bolsa del club", productService.getImageBlob("/static/images/bag.jpg")));
        productService.save(new Product("BOLIGRAFO BCF", 2, "Boligrafo del club", productService.getImageBlob("/static/images/pen.jpg")));
        productService.save(new Product("BRAGA CUELLO BCF", 12, "Braga de cuello del BCF", productService.getImageBlob("/static/images/ngaiter.jpg")));
        productService.save(new Product("GAFAS BCF", 30, "Protege tus ojos con estilo", productService.getImageBlob("/static/images/glasses.jpg")));
        productService.save(new Product("GUANTES LANA BCF", 30, "Abriga tus manos con los guantes blanquinegros", productService.getImageBlob("/static/images/gloves.jpg")));
        productService.save(new Product("LLAVERO DORADO BCF", 5, "Llavero del BCF", productService.getImageBlob("/static/images/keychain.jpg")));
        productService.save(new Product("MOCHILA ADIDAS BCF", 50, "Mochila Adidas del club", productService.getImageBlob("/static/images/backp.jpg")));
        productService.save(new Product("VISERA BCF", 10, "Gorra con el escudo del Burgos", productService.getImageBlob("/static/images/cap.jpg")));
        productService.save(new Product("PELUCHE OSO BCF", 20, "Abraza la calidez y ternura", productService.getImageBlob("/static/images/bear.jpg")));
    }
}
