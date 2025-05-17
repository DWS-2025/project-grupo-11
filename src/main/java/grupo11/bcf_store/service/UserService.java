package grupo11.bcf_store.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.User;
import grupo11.bcf_store.model.dto.UserDTO;
import grupo11.bcf_store.model.mapper.UserMapper;
import grupo11.bcf_store.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserMapper userMapper;

	public List<UserDTO> getAllUsers() {
		return userMapper.toDTOs(userRepository.findAll());
	}

	public UserDTO getUserById(long id) {
		User user = userRepository.findById(id).orElseThrow();
		return userMapper.toDTO(user);
	}

	public UserDTO createUser(UserDTO userDTO) {
		User user = userMapper.toDomain(userDTO);
		userRepository.save(user);
		return userMapper.toDTO(user);
	}

	public UserDTO updateUser(long id, UserDTO updatedUserDTO, HttpServletRequest request) {
		User updatedUser = userMapper.toDomain(updatedUserDTO);

		if (isSelf(request, id)) {
			User user = userRepository.findById(id).orElseThrow();
			user.setUsername(updatedUser.getUsername());
			user.setPassword(updatedUser.getPassword());
			user.setRoles(updatedUser.getRoles());
			userRepository.save(user);
			return userMapper.toDTO(user);
		}

		return null;
	}

	public UserDTO deleteUserById(long id) {
		User user = userRepository.findById(id).orElseThrow();
		if(user.getOrders() != null) {
			user.getOrders().forEach(order -> order.setUser(null));
		}
		if(user.getCart() != null) {
			user.getCart().setUser(null);
		}
		userRepository.deleteById(id);
		return userMapper.toDTO(user);
	}

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

	public boolean isSelf(HttpServletRequest request, long id) {
		if (request.getUserPrincipal() != null) {
			String name = request.getUserPrincipal().getName();
			User user = userRepository.findByUsername(name).orElseThrow();
			return user.getId() == id;
		}
		return false;
	}

	public long getCartIdByUsername(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		return user.getCart().getId();
	}

	public User findByUsername(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}

	public boolean existsByUsername(String username) {
		return userRepository.findByUsername(username).isPresent();
	}

	public void saveUser(User user) {
		userRepository.save(user);
	}

	public User findById(long id) {
		return userRepository.findById(id).orElse(null);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}
}
