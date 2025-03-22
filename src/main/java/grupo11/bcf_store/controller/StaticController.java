package grupo11.bcf_store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import grupo11.bcf_store.repository.CartRepository;

@Controller
public class StaticController {

    @Autowired
    private CartRepository cartRepository;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}
