package grupo11.bcf_store;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @GetMapping("/cart.html")
    public String cart(Model model) {

        model.addAttribute("name", "Admin");

        return "cart";
    }
}
