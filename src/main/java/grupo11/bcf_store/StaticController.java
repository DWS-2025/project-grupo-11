package grupo11.bcf_store;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController {

    @GetMapping("/index.html")
    public String index(Model model) {
        model.addAttribute("name", "Admin");

        return "index";
    }

    @GetMapping("/add.html")
    public String addProductForm() {
        return "add";
    }
}
