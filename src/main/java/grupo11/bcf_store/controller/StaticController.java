package grupo11.bcf_store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}
