package grupo11.bcf_store.security.jwt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.User;
import grupo11.bcf_store.model.Cart;
import grupo11.bcf_store.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserRegisterService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<AuthResponse> register(HttpServletResponse response, RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(new AuthResponse(AuthResponse.Status.FAILURE, "El usuario ya existe."));
        }

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setRoles(List.of("USER"));
        newUser.setFullName(registerRequest.getFullName());
        newUser.setDescription(registerRequest.getDescription());

        // Crear y asociar un carrito al nuevo usuario
        Cart newCart = new Cart();
        newUser.setCart(newCart);

        userRepository.save(newUser);

        var newAccessToken = jwtTokenProvider.generateAccessToken(new org.springframework.security.core.userdetails.User(
                newUser.getUsername(), newUser.getPassword(), List.of()));
        var newRefreshToken = jwtTokenProvider.generateRefreshToken(new org.springframework.security.core.userdetails.User(
                newUser.getUsername(), newUser.getPassword(), List.of()));

        response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));
        response.addCookie(buildTokenCookie(TokenType.REFRESH, newRefreshToken));

        return ResponseEntity.ok(new AuthResponse(AuthResponse.Status.SUCCESS, "Registro exitoso. Tokens creados en cookies."));
    }

    private Cookie buildTokenCookie(TokenType type, String token) {
        Cookie cookie = new Cookie(type.cookieName, token);
        cookie.setMaxAge((int) type.duration.getSeconds());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
}
