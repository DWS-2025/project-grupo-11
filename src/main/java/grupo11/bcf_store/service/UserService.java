package grupo11.bcf_store.service;

import java.util.List;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import grupo11.bcf_store.model.User;
import grupo11.bcf_store.model.dto.UserDTO;
import grupo11.bcf_store.model.mapper.UserMapper;
import grupo11.bcf_store.repository.UserRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

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
			user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
			// Use the same validation as updateUserInfo
			try {
				updateUserInfo(user, updatedUser.getFullName(), updatedUser.getDescription());
			} catch (IllegalArgumentException e) {
				return null;
			}
			userRepository.save(user);
			return userMapper.toDTO(user);
		}

		return null;
	}

	public void updateUserInfo(User user, String fullName, String description) {
		user.setFullName(fullName);

		if (description != null) {
			Pattern forbiddenPattern = Pattern.compile(
					"(&#\\d+;|&#x[0-9a-fA-F]+;|\\\\u[0-9a-fA-F]{4}|\\+ADw-|\\+AD4-)",
					Pattern.CASE_INSENSITIVE);

			if (forbiddenPattern.matcher(description).find()) {
				throw new IllegalArgumentException("La descripción contiene codificaciones no permitidas.");
			}

			Safelist safelist = Safelist.none()
					.addTags("b", "i", "u", "a", "ul", "ol", "li", "strong", "em", "br")
					.addAttributes("a", "target", "rel");

			String safeDescription = Jsoup.clean(description, safelist);
			
			if (!safeDescription.equals(description)) {
				throw new IllegalArgumentException("La descripción tiene cáracteres inválidos.");
			}
			user.setDescription(safeDescription);
		} else {
			user.setDescription(null);
		}
	}

	public UserDTO deleteUserById(long id) {
		User user = userRepository.findById(id).orElseThrow();
		if (user.getOrders() != null) {
			user.getOrders().forEach(order -> order.setUser(null));
		}
		if (user.getCart() != null) {
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

	// Get logged in user roles
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

	public boolean canAccessUser(HttpServletRequest request, long userId) {
		return isSelf(request, userId) || isAdmin(request);
	}

	public void uploadDni(long userId, MultipartFile dniFile, HttpServletRequest request) {
		User user = userRepository.findById(userId).orElseThrow();
		// Allow only the user to upload the DNI
		if (!canAccessUser(request, userId)) {
			throw new SecurityException("No autorizado");
		}
		if (dniFile == null || dniFile.isEmpty()) {
			throw new IllegalArgumentException("No se ha seleccionado ningún archivo.");
		}
		// Limit the file size to 5 MB
		if (dniFile.getSize() > 5 * 1024 * 1024) {
			throw new IllegalArgumentException("El archivo es demasiado grande (máximo 5 MB).");
		}
		String originalFilename = dniFile.getOriginalFilename();
		if (originalFilename == null || originalFilename.contains("..")) {
			throw new IllegalArgumentException("Nombre de archivo no válido.");
		}
		originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
		String storedFilename = user.getId() + "_" + originalFilename;
		String tempFilename = "temp_" + System.currentTimeMillis() + "_" + storedFilename;
		Path tempFilePath = Paths.get("dni").resolve(tempFilename);

		try {
			Path dniDirPath = Paths.get("dni");
			if (!Files.exists(dniDirPath)) {
				Files.createDirectories(dniDirPath);
			}
			String previousDni = user.getDniOriginalFilename();
			if (previousDni != null && !previousDni.isBlank()) {
				Path previousFilePath = dniDirPath.resolve(previousDni);
				try {
					Files.deleteIfExists(previousFilePath);
				} catch (Exception ignore) {
				}
			}
			dniFile.transferTo(tempFilePath);
			String mimeType = Files.probeContentType(tempFilePath);
			if (mimeType == null || !mimeType.equals("image/jpeg")) {
				Files.deleteIfExists(tempFilePath);
				throw new IllegalArgumentException("El archivo subido no es una imagen válida.");
			}
			BufferedImage image = ImageIO.read(tempFilePath.toFile());
			if (image == null) {
				Files.deleteIfExists(tempFilePath);
				throw new IllegalArgumentException("El archivo subido no es una imagen válida.");
			}
			Path filePath = dniDirPath.resolve(storedFilename);
			Files.move(tempFilePath, filePath);
			user.setDniOriginalFilename(storedFilename);
			userRepository.save(user);
		} catch (Exception e) {
			throw new IllegalArgumentException("Error al guardar el archivo con ese nombre.");
		} finally {
			try {
				Files.deleteIfExists(tempFilePath);
			} catch (Exception ignore) {
			}
		}
	}

	public ResponseEntity<byte[]> downloadDni(long userId, jakarta.servlet.http.HttpServletRequest request) {
		User user = userRepository.findById(userId).orElseThrow();
		if (!canAccessUser(request, userId)) {
			throw new SecurityException("No autorizado");
		}
		if (user.getDniOriginalFilename() == null) {
			throw new java.util.NoSuchElementException("DNI no encontrado");
		}
		try {
			Path filePath = Paths.get("dni").resolve(user.getDniOriginalFilename());
			if (!Files.exists(filePath)) {
				throw new java.util.NoSuchElementException("DNI no encontrado");
			}
			byte[] fileBytes = Files.readAllBytes(filePath);
			String contentType = Files.probeContentType(filePath);
			if (contentType == null) {
				contentType = "application/octet-stream";
			}
			String storedFilename = user.getDniOriginalFilename();
			String originalFilename = storedFilename.substring(storedFilename.indexOf('_') + 1);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
					.contentType(MediaType.parseMediaType(contentType))
					.body(fileBytes);
		} catch (Exception e) {
			throw new RuntimeException("Error al descargar el DNI");
		}
	}

	public String encodePassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}
}
