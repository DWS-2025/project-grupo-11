package grupo11.bcf_store.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import grupo11.bcf_store.security.jwt.UserRegisterService;
import grupo11.bcf_store.security.jwt.RegisterRequest;
import grupo11.bcf_store.service.UserService;
import grupo11.bcf_store.security.jwt.*;
import grupo11.bcf_store.security.jwt.AuthResponse.Status;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/api/auth/")
public class LoginRestController {
	
	@Autowired
    private UserRegisterService userRegisterService;

	@Autowired
	private UserLoginService userLoginService;

	@Autowired
	private UserService userService;

    @PostMapping("/register/")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest registerRequest,
            HttpServletResponse response) {
        try {
            userService.validateDescription(registerRequest.getDescription());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(AuthResponse.Status.FAILURE, e.getMessage()));
        }
        return userRegisterService.register(response, registerRequest);
    }

	@PostMapping("/login/")
	public ResponseEntity<AuthResponse> login(
			@RequestBody LoginRequest loginRequest,
			HttpServletResponse response) {
		
		return userLoginService.login(response, loginRequest);
	}

	@PostMapping("/refresh/")
	public ResponseEntity<AuthResponse> refreshToken(
			@CookieValue(name = "RefreshToken", required = false) String refreshToken, HttpServletResponse response) {

		return userLoginService.refresh(response, refreshToken);
	}

	@PostMapping("/logout/")
	public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
		return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userLoginService.logout(response)));
	}
}