package grupo11.bcf_store.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.crypto.password.PasswordEncoder;
import grupo11.bcf_store.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import grupo11.bcf_store.model.User;
import grupo11.bcf_store.model.Cart;
import grupo11.bcf_store.service.OrderService;
import grupo11.bcf_store.service.UserService;
import grupo11.bcf_store.service.CartService;

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

    @Autowired
    private CartService cartService;

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
    public String registerPage(Model model) {
        model.addAttribute("description", "");
        return "register";
    }

    @PostMapping("/register/")
    public String performRegister(
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam String fullName,
        @RequestParam String description,
        Model model
    ) {
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("errorMessage", "El usuario ya existe.");
            return "error";
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(List.of("USER"));
        newUser.setCart(new Cart());
        newUser.setFullName(fullName);
        newUser.setDescription(description);
        userRepository.save(newUser);
        return "redirect:/myaccount/";
    }

    @GetMapping("/private/")
    public String privatePage(Model model, HttpServletRequest request) {
        String name = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(name).orElseThrow();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("fullName", user.getFullName());
        model.addAttribute("description", user.getDescription() != null ? user.getDescription() : "");
        return "private";
    }

    @PostMapping("/update-user/")
    public String updateUser(
        @RequestParam(required = false) String fullName,
        @RequestParam(required = false) String description,
        HttpServletRequest request
    ) {
        String currentUsername = userService.getLoggedInUsername(request);
        User user = userRepository.findByUsername(currentUsername).orElseThrow();

        user.setFullName(fullName);
        user.setDescription(description);
        userRepository.save(user);

        return "redirect:/private/";
    }

    @PostMapping("/update-credentials/")
    public String updateCredentials(
        @RequestParam String username,
        @RequestParam String password,
        HttpServletRequest request
    ) {
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
    public String adminPage(Model model) {
        var users = userRepository.findAll().stream().map(user -> {
            String safeDescription = user.getDescription() != null ? user.getDescription() : "";
            return new Object() {
                public final long id = user.getId();
                public final String username = user.getUsername();
                public final String fullName = user.getFullName();
                public final String description = safeDescription;
                public final String roles = user.getRoles() != null ? user.getRoles().toString() : "";
            };
        }).toList();
        model.addAttribute("users", users);

        // Add cart information for each cart
        var carts = userRepository.findAll().stream().map(user -> {
            var cart = user.getCart();
            return new Object() {
                public final long id = cart.getId();
                public final String username = user.getUsername();
                public final String fullName = user.getFullName();
                public final String description = "";
                public final int totalItems = cartService.getTotalItemsInCart(cart.getId());
                public final double totalPrice = cartService.getTotalPrice(cart.getId());
            };
        }).toList();
        model.addAttribute("carts", carts);
        return "admin";
    }

    @PostMapping("/admin/delete-user/{id}/")
    public String adminDeleteUser(@PathVariable long id, HttpServletRequest request, Model model) {
        // Prevent admin from deleting their own account
        String currentUsername = userService.getLoggedInUsername(request);
        User userToDelete = userRepository.findById(id).orElse(null);
        if (userToDelete == null) {
            model.addAttribute("errorMessage", "Usuario no encontrado.");
            return "error";
        }
        if (userToDelete.getUsername().equals(currentUsername)) {
            model.addAttribute("errorMessage", "No puedes eliminar tu propia cuenta desde el panel de administrador.");
            return "error";
        }
        // Delete all orders of the user
        userToDelete.getOrders().forEach(order -> orderService.remove(order.getId()));
        userService.deleteUserById(id);
        return "redirect:/admin/";
    }
}