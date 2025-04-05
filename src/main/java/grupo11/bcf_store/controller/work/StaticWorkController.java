package grupo11.bcf_store.controller.work;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticWorkController {

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/contact/")
    public String contact() {
        return "contact";
    }
}
