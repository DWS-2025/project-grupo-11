package grupo11.bcf_store.security.jwt;

public class RegisterRequest {

    private String username;
    private String password;
    private String fullName;
    private String description;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, String fullName, String description) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
	public String toString() {
		return "LoginRequest [username=" + username + ", password=" + password + ", fullName=" + fullName + ", description=" + description + "]";
	}
}
