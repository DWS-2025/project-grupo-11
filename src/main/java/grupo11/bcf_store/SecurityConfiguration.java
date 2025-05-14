package grupo11.bcf_store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import grupo11.bcf_store.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
    public UserService userService;

    //@Value("${security.user}")
	private String username;

	//@Value("${security.encodedPassword}")
	private String encodedPassword;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		UserDetails user = User.builder()
				.username("user")
				.password(passwordEncoder().encode("pass"))
				.roles("USER")
				.build();
		UserDetails prueba = User.builder()
				.username("prueba")
				.password(passwordEncoder().encode("prueba"))
				.roles("USER")
				.build();
		UserDetails admin = User.builder()
				.username("admin")
				.password(passwordEncoder().encode("adminpass"))
				.roles("USER","ADMIN")
				.build();
		return new InMemoryUserDetailsManager(user, prueba, admin);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http
			.authorizeHttpRequests(authorize -> authorize
					// PUBLIC PAGES
					.requestMatchers("/").permitAll()
					.requestMatchers("/clothes/**").permitAll()
					.requestMatchers("/view/**").permitAll()
					.requestMatchers("/product-image/**").permitAll()
					.requestMatchers("/search-products/**").permitAll()
					.requestMatchers("/myaccount/**").permitAll()
					.requestMatchers("/contact/**").permitAll()
					.requestMatchers("/images/**").permitAll()
					.requestMatchers("/css/**").permitAll()
					.requestMatchers("/js/**").permitAll()
					.requestMatchers("/favicon.ico").permitAll()
					.requestMatchers("/register/**").permitAll()

					// USER PAGES
					.requestMatchers("/cart/**").hasRole("USER")
					.requestMatchers("/add-to-cart/**").hasRole("USER")
					.requestMatchers("/remove-from-cart/**").hasRole("USER")
					.requestMatchers("/orders/**").hasRole("USER")
					.requestMatchers("/create-order/**").hasRole("USER")
					.requestMatchers("/view-order/**").hasRole("USER")
					.requestMatchers("/private/**").hasRole("USER")
					.requestMatchers("/update-user/**").hasRole("USER")
					.requestMatchers("/delete-user/**").hasRole("USER")

					// ADMIN PAGES
					.requestMatchers("/admin/**").hasRole("ADMIN")
					.requestMatchers("/add-product/**").hasRole("ADMIN")
					.requestMatchers("/edit-product/**").hasRole("ADMIN")
					.requestMatchers("/delete-product/**").hasRole("ADMIN")
					.requestMatchers("/delete-order/**").hasRole("ADMIN")
			)
			.formLogin(formLogin -> formLogin
					.loginPage("/login/")
					.failureUrl("/loginerror/")
					.defaultSuccessUrl("/private/")
					.permitAll()
			)
			
			.logout(logout -> logout
					.logoutUrl("/logout/")
					.logoutSuccessUrl("/myaccount/")
					.permitAll()
			);
		
		// Disable CSRF protection
		http.csrf(csrf -> csrf.disable());

		return http.build();
	}
    
}
