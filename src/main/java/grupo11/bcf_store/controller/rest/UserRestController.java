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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import grupo11.bcf_store.model.dto.UserDTO;
import grupo11.bcf_store.service.UserService;

@RestController
@RequestMapping("/api/users/")
public class UserRestController {

    @Autowired
    private UserService userService;

    // API methods
    @GetMapping("/")
    public List<UserDTO> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}/")
    public UserDTO getUser(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO savedUser = userService.createUser(userDTO);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.id()).toUri();
        return ResponseEntity.created(location).body(savedUser);
    }

    @PutMapping("/{id}/info/")
    public ResponseEntity<?> updateUserInfo(
            @PathVariable long id,
            @RequestBody UserDTO updatedUserDTO,
            HttpServletRequest request) {
        if (!userService.canAccessUser(request, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }
        try {
            var user = userService.findById(id);
            if (user == null) return ResponseEntity.notFound().build();
            userService.updateUserInfo(user, updatedUserDTO.fullName(), updatedUserDTO.description());
            userService.saveUser(user);
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/credentials/")
    public ResponseEntity<?> updateUserCredentials(
            @PathVariable long id,
            @RequestBody UserDTO updatedUserDTO,
            HttpServletRequest request) {
        if (!userService.canAccessUser(request, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }
        var user = userService.findById(id);
        if (user == null) return ResponseEntity.notFound().build();
        if (updatedUserDTO.username() == null || updatedUserDTO.password() == null) {
            return ResponseEntity.badRequest().body("Usuario y contraseña requeridos");
        }
        user.setUsername(updatedUserDTO.username());
        user.setPassword(userService.encodePassword(updatedUserDTO.password()));
        userService.saveUser(user);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Transactional
    @DeleteMapping("/{id}/")
    public UserDTO deleteUser(@PathVariable long id, HttpServletRequest request) {
        if (!userService.canAccessUser(request, id)) {
            throw new SecurityException("No autorizado");
        }
        return userService.deleteUserById(id);
    }

    @PostMapping("/{id}/dni/")
    public ResponseEntity<?> uploadDni(
            @PathVariable long id,
            @RequestParam("dniFile") MultipartFile dniFile,
            HttpServletRequest request) {
        if (!userService.canAccessUser(request, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }
        try {
            userService.uploadDni(id, dniFile, request);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al subir el DNI");
        }
    }

    @GetMapping("/{id}/dni/")
    public ResponseEntity<?> getDni(@PathVariable long id, HttpServletRequest request) {
        if (!userService.canAccessUser(request, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }
        try {
            // The method downloadDni() in UserService returns a ResponseEntity<byte[]>
            return userService.downloadDni(id, request);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("DNI no encontrado");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al descargar el DNI");
        }
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
