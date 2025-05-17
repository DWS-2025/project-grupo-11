package grupo11.bcf_store.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import grupo11.bcf_store.model.User;
import grupo11.bcf_store.model.Cart;
import grupo11.bcf_store.service.OrderService;
import grupo11.bcf_store.service.UserService;
import grupo11.bcf_store.service.CartService;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Controller
public class UserWebController {

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
        model.addAttribute("users", userService.getAllUsers());
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
        User user = userService.findByUsername(username);
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
            Model model) {
        if (userService.existsByUsername(username)) {
            model.addAttribute("errorMessage", "El usuario ya existe.");
            return "error";
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(List.of("USER"));
        newUser.setCart(new Cart());
        newUser.getCart().setUser(newUser); // <-- Añadir esta línea
        newUser.setFullName(fullName);
        newUser.setDescription(description);
        userService.saveUser(newUser);
        return "redirect:/myaccount/";
    }

    @GetMapping("/private/")
    public String privatePage(Model model, HttpServletRequest request) {
        String name = userService.getLoggedInUsername(request);
        User user = userService.findByUsername(name);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("admin", userService.isAdmin(request));
        model.addAttribute("fullName", user.getFullName());
        model.addAttribute("description", user.getDescription() != null ? user.getDescription() : "");
        String dniOriginalFilename = user.getDniOriginalFilename();
        String dniDisplayName = (dniOriginalFilename != null && dniOriginalFilename.contains("_"))
                ? dniOriginalFilename.substring(dniOriginalFilename.indexOf('_') + 1)
                : dniOriginalFilename;
        model.addAttribute("dniOriginalFilename", dniDisplayName);
        return "private";
    }

    @PostMapping("/upload-dni/")
    public String uploadDni(@RequestParam("dniFile") MultipartFile dniFile, HttpServletRequest request, Model model) {
        String currentUsername = userService.getLoggedInUsername(request);
        User user = userService.findByUsername(currentUsername);
        try {
            userService.uploadDni(user.getId(), dniFile, request);
        } catch (SecurityException e) {
            model.addAttribute("errorMessage", "No autorizado.");
            return "error";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al guardar el archivo con ese nombre.");
            return "error";
        }
        return "redirect:/private/";
    }

    @GetMapping("/download-dni/")
    @ResponseBody
    public ResponseEntity<byte[]> downloadDni(HttpServletRequest request) {
        String currentUsername = userService.getLoggedInUsername(request);
        User user = userService.findByUsername(currentUsername);
        try {
            return userService.downloadDni(user.getId(), request);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/update-user/")
    public String updateUser(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String description,
            HttpServletRequest request, Model model) {
        String currentUsername = userService.getLoggedInUsername(request);
        User user = userService.findByUsername(currentUsername);

        if (!userService.canAccessUser(request, user.getId())) {
            model.addAttribute("errorMessage", "No autorizado.");
            return "error";
        }

        user.setFullName(fullName);
        user.setDescription(description);
        userService.saveUser(user);

        return "redirect:/private/";
    }

    @PostMapping("/update-credentials/")
    public String updateCredentials(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request, Model model) {
        String currentUsername = userService.getLoggedInUsername(request);
        User user = userService.findByUsername(currentUsername);

        if (!userService.canAccessUser(request, user.getId())) {
            model.addAttribute("errorMessage", "No autorizado.");
            return "error";
        }

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userService.saveUser(user);

        request.getSession().invalidate();
        return "redirect:/login/";
    }

    @PostMapping("/delete-user/")
    public String deleteUser(HttpServletRequest request, Model model) {
        String currentUsername = userService.getLoggedInUsername(request);
        User user = userService.findByUsername(currentUsername);

        if (!userService.canAccessUser(request, user.getId())) {
            model.addAttribute("errorMessage", "No autorizado.");
            return "error";
        }

        // Prevent admin from deleting their own account
        if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
            model.addAttribute("errorMessage", "No puedes eliminar tu propia cuenta siendo administrador.");
            return "error";
        }

        // Delete all orders of the user
        user.getOrders().forEach(order -> orderService.remove(order.getId()));

        userService.deleteUserById(user.getId());
        request.getSession().invalidate(); // Close the session
        return "redirect:/";
    }

    @GetMapping("/admin/")
    public String adminPage(Model model) {
        // Obtain the list of users
        List<User> userEntities = userService.findAll(); // Ensure this method returns a list of User entities

        var users = userEntities.stream().map(user -> {
            String safeDescription = user.getDescription() != null ? user.getDescription() : "";
            String dniOriginalFilename = user.getDniOriginalFilename();
            String dniDisplayName = (dniOriginalFilename != null && dniOriginalFilename.contains("_"))
                    ? dniOriginalFilename.substring(dniOriginalFilename.indexOf('_') + 1)
                    : dniOriginalFilename;
            return new Object() {
                public final long id = user.getId();
                public final String username = user.getUsername();
                public final String fullName = user.getFullName();
                public final String description = safeDescription;
                public final String roles = user.getRoles() != null ? user.getRoles().toString() : "";
                public final String dniOriginalFilename = dniDisplayName;
            };
        }).toList();
        model.addAttribute("users", users);

        var carts = userEntities.stream().map(user -> {
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
        User userToDelete = userService.findById(id);
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