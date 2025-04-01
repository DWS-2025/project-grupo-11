package grupo11.bcf_store.controller;

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

import grupo11.bcf_store.model.User;
import grupo11.bcf_store.model.UserDTO;
import grupo11.bcf_store.model.UserMapper;
import grupo11.bcf_store.repository.UserRepository;

@RestController
@RequestMapping("/api/users/")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

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
        User user = toDomain(userDTO);
        userRepository.save(user);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).body(toDTO(user));
    }

    @PutMapping("/{id}/")
    public UserDTO replaceUser(@PathVariable long id, @RequestBody UserDTO updatedUserDTO) {
        if (userRepository.existsById(id)) {
            User updatedUser = toDomain(updatedUserDTO);
            updatedUser.setId(id);
            userRepository.save(updatedUser);
            return toDTO(updatedUser);
        } else {
            throw new NoSuchElementException();
        }
    }

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
