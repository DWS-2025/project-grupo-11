package grupo11.bcf_store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import grupo11.bcf_store.service.UserService;

@Configuration
@EnableWebSecurity
public class RestSecurityConfig {

	@Autowired
    public UserService userService;

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
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http.authorizeHttpRequests(authorize -> authorize
                    // PUBLIC ENDPOINTS
					.anyRequest().permitAll()
                    // USER ENDPOINTS
                    
                    // ADMIN ENDPOINTS
                    .requestMatchers(HttpMethod.POST,"/api/products/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT,"/api/products/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE,"/api/products/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST,"/api/products/**/image/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT,"/api/products/**/image/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE,"/api/products/**/image/**").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST,"/api/orders/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE,"/api/orders/**").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST,"/api/carts/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE,"/api/carts/**").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST,"/api/users/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE,"/api/users/**").hasRole("ADMIN")
			);
		
        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Enable Basic Authentication
        http.httpBasic(Customizer.withDefaults());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}

}
