package grupo11.bcf_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(
   pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class BcfStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BcfStoreApplication.class, args);
	}

}
