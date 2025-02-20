package grupo11.bcf_store;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductViewController {

    @GetMapping("/product_view.html")
    public String product_view(Model model) {

        model.addAttribute("product_name", "}}");

        return "product_view";
    }
}
