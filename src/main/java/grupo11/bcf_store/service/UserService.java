package grupo11.bcf_store.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.User;
import grupo11.bcf_store.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;


	public String getLoggedInUsername(HttpServletRequest request) {
		if (request.getUserPrincipal() != null) {
			String name = request.getUserPrincipal().getName();
			User user = userRepository.findByUsername(name).orElseThrow();
			return user.getUsername();
		}
		return null;
	}

	// Get logged in user id
	public long getLoggedInUserId(HttpServletRequest request) {
		if (request.getUserPrincipal() != null) {
			String name = request.getUserPrincipal().getName();
			User user = userRepository.findByUsername(name).orElseThrow();
			return user.getId();
		}
		return 0;
	}

	//Get logged in user roles
	public List<String> getLoggedInUserRoles(HttpServletRequest request) {
		if (request.getUserPrincipal() != null) {
			String name = request.getUserPrincipal().getName();
			User user = userRepository.findByUsername(name).orElseThrow();
			return user.getRoles();
		}
		return null;
	}

	public boolean isAdmin(HttpServletRequest request) {
		if (request.getUserPrincipal() != null) {
			String name = request.getUserPrincipal().getName();
			User user = userRepository.findByUsername(name).orElseThrow();
			return user.getRoles().contains("ADMIN");
		}
		return false;
	}

	public long getCartIdByUsername(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		return user.getCart().getId();
	}

	public void deleteUserById(long id) {
		userRepository.deleteById(id);
	}
}
