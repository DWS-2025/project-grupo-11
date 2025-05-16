package grupo11.bcf_store.controller.rest;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import org.springframework.security.crypto.password.PasswordEncoder;

import grupo11.bcf_store.model.User;
import grupo11.bcf_store.model.dto.UserDTO;
import grupo11.bcf_store.model.mapper.UserMapper;
import grupo11.bcf_store.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/users/")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // API methods
    @GetMapping("/")
    public List<UserDTO> getUsers() {
        return toDTOs(userRepository.findAll());
    }

    @GetMapping("/{id}/")
    public UserDTO getUser(@PathVariable long id) {
        return toDTO(userRepository.findById(id).orElseThrow());
    }

    @PostMapping("/")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        // Validation of mandatory fields
        if (userDTO.username() == null || userDTO.username().isBlank() ||
            userDTO.password() == null || userDTO.password().isBlank() ||
            userDTO.fullName() == null || userDTO.fullName().isBlank() ||
            userDTO.description() == null || userDTO.description().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        User user = toDomain(userDTO);

        // Forces the user to be created with the USER role
        user.setRoles(java.util.List.of("USER"));

        // Encrypt the password
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Ensure the user has a cart assigned
        if (user.getCart() == null) {
            user.setCart(new grupo11.bcf_store.model.Cart());
        }

        userRepository.save(user);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).body(toDTO(user));
    }

    @PutMapping("/{id}/")
    public ResponseEntity<UserDTO> replaceUser(@PathVariable long id, @RequestBody UserDTO updatedUserDTO, HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String currentUsername = request.getUserPrincipal().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        boolean isAdmin = request.isUserInRole("ADMIN");
        boolean isSelf = currentUser.getId() == id;

        if (!isAdmin && !isSelf) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Validation of mandatory fields
        if (updatedUserDTO.username() == null || updatedUserDTO.username().isBlank() ||
            updatedUserDTO.password() == null || updatedUserDTO.password().isBlank() ||
            updatedUserDTO.fullName() == null || updatedUserDTO.fullName().isBlank() ||
            updatedUserDTO.description() == null || updatedUserDTO.description().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (userRepository.existsById(id)) {
            User updatedUser = toDomain(updatedUserDTO);
            updatedUser.setId(id);

            // Ensure the user has a cart assigned
            if (updatedUser.getCart() == null) {
                updatedUser.setCart(new grupo11.bcf_store.model.Cart());
            }

            // Encrypt the password
            String rawPassword = updatedUser.getPassword();
            if (rawPassword != null && !rawPassword.startsWith("$2a$")) { // BCrypt hash check
                updatedUser.setPassword(passwordEncoder.encode(rawPassword));
            }

            // Assign roles based on the current user's role
            User originalUser = userRepository.findById(id).orElseThrow();
            if (originalUser.getRoles().contains("ADMIN") && isSelf && isAdmin) {
                updatedUser.setRoles(java.util.List.of("ADMIN"));
            } else {
                updatedUser.setRoles(java.util.List.of("USER"));
            }

            userRepository.save(updatedUser);
            return ResponseEntity.ok(toDTO(updatedUser));
        } else {
            throw new NoSuchElementException();
        }
    }

    @Transactional
    @DeleteMapping("/{id}/")
    public UserDTO deleteUser(@PathVariable long id) {
        User user = userRepository.findById(id).orElseThrow();
        userRepository.deleteById(id);
        return toDTO(user);
    }

    // DTO methods
    private UserDTO toDTO(User user) {
        return mapper.toDTO(user);
    }

    private User toDomain(UserDTO userDTO) {
        return mapper.toDomain(userDTO);
    }

    private List<UserDTO> toDTOs(List<User> users) {
        return mapper.toDTOs(users);
    }

    // Exception handling
    @ControllerAdvice
    class NoSuchElementExceptionControllerAdvice {
        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(NoSuchElementException.class)
        public void handleNoTFound() {
        }
    }
}
