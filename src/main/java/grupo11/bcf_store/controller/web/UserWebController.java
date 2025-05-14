package grupo11.bcf_store.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.password.PasswordEncoder;
import grupo11.bcf_store.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import grupo11.bcf_store.model.User;
import grupo11.bcf_store.model.Cart;
import grupo11.bcf_store.service.OrderService;
import grupo11.bcf_store.service.UserService;

import java.util.List;

@Controller
public class UserWebController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/users/")
    public String getUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }

    @GetMapping("/myaccount/")
    public String myAccount(Model model, HttpServletRequest request) {
        model.addAttribute("username", userService.getLoggedInUsername(request));
        model.addAttribute("admin", userService.isAdmin(request));
        return "myaccount";
    }

    @GetMapping("/login/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login/")
    public String performLogin(@RequestParam String username, @RequestParam String password, Model model) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return "redirect:/myaccount/";
        }
        model.addAttribute("error", "Invalid username or password");
        return "redirect:/loginerror/";
    }

    @GetMapping("/loginerror/")
    public String loginErrorPage(Model model) {
        model.addAttribute("errorMessage", "Contraseña o usuario incorrecto.");
        return "error";
    }

    @GetMapping("/register/")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register/")
    public String performRegister(@RequestParam String username, @RequestParam String password, Model model) {
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("errorMessage", "El usuario ya existe.");
            return "error";
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(List.of("USER"));
        newUser.setCart(new Cart()); 
        userRepository.save(newUser);
        return "redirect:/myaccount/";
    }

    @GetMapping("/private/")
    public String privatePage(Model model, HttpServletRequest request) {
        String name = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(name).orElseThrow();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        return "private";
    }

    @PostMapping("/update-user/")
    public String updateUser(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        String currentUsername = userService.getLoggedInUsername(request);
        User user = userRepository.findByUsername(currentUsername).orElseThrow();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        request.getSession().invalidate();
        return "redirect:/login/";
    }

    @PostMapping("/delete-user/")
    public String deleteUser(HttpServletRequest request) {
        String currentUsername = userService.getLoggedInUsername(request);
        User user = userRepository.findByUsername(currentUsername).orElseThrow();

        // Delete all orders of the user
        user.getOrders().forEach(order -> orderService.remove(order.getId()));

        userService.deleteUserById(user.getId());
        request.getSession().invalidate(); // Close the session
        return "redirect:/";
    }

    @GetMapping("/admin/")
    public String adminPage() {
        return "admin";
    }
}