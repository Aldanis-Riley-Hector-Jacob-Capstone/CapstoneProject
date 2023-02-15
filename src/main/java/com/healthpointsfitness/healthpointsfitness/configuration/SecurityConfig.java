package com.healthpointsfitness.healthpointsfitness.configuration;

import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.UserDetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Autowired
    private UserRepository usersDao;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //Configure cors
//                .cors().and()
//                .csrf().disable()

                //Allow public access to all public routes

                .authorizeHttpRequests()
                .requestMatchers(
                        "/changePass",
                        "/login",
                        "/register",
                        "/recover",
                        "/landing",
                        "/js/**",
                        "/img/**",
                        "/css/**",
                        "/static/**",
                        "/freechat",
                        "/chat",
                        "/",
                        "/user/api/v1/recoveryCodeCheck",
                        "/changePassword"
                )
                .permitAll()

                //Lock down user routes
                .and()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/users/**",
                        "/users",
                        "/profile",
                        "/users/api/v1/**",
                        "/users/api/v1/search_friends",
                        "/admin/api/v1/completeExercise"
                ).hasAnyAuthority("ROLE_CLIENT","ROLE_ADMIN")

                //Lock down admin routes
                .and()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/admin/**",
                        "/admin/api/v1/**",
                        "admin/path/delete/{id}",
                        "/admin/api/v1/findExerciseByMuscle"
                        ).hasAuthority("ROLE_ADMIN")

                //Block all other requests
                .and()
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()

                //Configure Login
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/")

                //Configure Logout
                .and()
                .logout()
                .logoutSuccessUrl("/landing?logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")

                //Configure session creation
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        return http.build();
    }

    @Bean
    AuthenticationProvider authProv() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(new UserDetailsLoader(usersDao));
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowCredentials(true);
        config.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}