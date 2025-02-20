package grupo11.bcf_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BcfStoreApplication {
	
	@Bean
	public Cart cart() {
		return new Cart();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(BcfStoreApplication.class, args);
	}

}
