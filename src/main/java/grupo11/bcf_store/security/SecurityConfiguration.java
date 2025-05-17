package grupo11.bcf_store.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import grupo11.bcf_store.security.jwt.JwtRequestFilter;
import grupo11.bcf_store.security.jwt.UnauthorizedHandlerJwt;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
    public RepositoryUserDetailsService userDetailService;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfiguration(@Lazy JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	@Order(1)
	public SecurityFilterChain restFilterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http
			.securityMatcher("/api/**")
			.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

		http
			.authorizeHttpRequests(authorize -> authorize
					// PUBLIC ENDPOINTS
					.requestMatchers(HttpMethod.GET, "/api/products/").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/products/{id}/").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/products/{id}/image/").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/products/search/**").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/auth/login/").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/auth/refresh/").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/auth/logout/").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/auth/register/").permitAll()

					// USER ENDPOINTS
					.requestMatchers(HttpMethod.GET, "/api/orders/**").hasRole("USER")

					.requestMatchers(HttpMethod.GET, "/api/carts/**").hasRole("USER")
					
					.requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("USER")
					.requestMatchers(HttpMethod.DELETE, "/api/users/{id}/").hasRole("USER")
					.requestMatchers(HttpMethod.PUT, "/api/users/{id}/info/").hasRole("USER")
					.requestMatchers(HttpMethod.PUT, "/api/users/{id}/credentials/").hasRole("USER")
					.requestMatchers(HttpMethod.GET, "/api/users/{id}/dni/").hasRole("USER")
					.requestMatchers(HttpMethod.POST, "/api/users/{id}/dni/").hasRole("USER")
					

					// ADMIN ENDPOINTS
					.requestMatchers(HttpMethod.POST, "/api/products/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PUT, "/api/products/{id}/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE, "/api/products/{id}/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST, "/api/products/{id}/image/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PUT, "/api/products/{id}/image/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE, "/api/products/{id}/image/").hasRole("ADMIN")

					.requestMatchers(HttpMethod.POST, "/api/orders/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE, "/api/orders/{id}/").hasRole("ADMIN")

					.requestMatchers(HttpMethod.POST, "/api/carts/").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE, "/api/carts/{id}/").hasRole("ADMIN")
			);
		
        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Token filter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
		
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
					.requestMatchers("/update-credentials/**").hasRole("USER")
					.requestMatchers("/delete-user/**").hasRole("USER")
					.requestMatchers("/upload-dni/**").hasRole("USER")
					.requestMatchers("/download-dni/**").hasRole("USER")

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
