package grupo11.bcf_store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import grupo11.bcf_store.repository.UserRepository;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public String getUsers(Model model){
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }
}
