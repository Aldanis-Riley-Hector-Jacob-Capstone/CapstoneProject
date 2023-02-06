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

@Configuration
@EnableWebSecurity
class SecurityConfig {
        @Autowired
        private UserRepository usersDao;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
            .cors().and()
            .csrf().disable()
//            .and()
            .authorizeHttpRequests()
            .requestMatchers(
                "/login",
                    "/register",
//                    "/static/css/img/**",
                    "/js/**",
                    "/static/css/**",
                    "/img/**"
            )
            .permitAll()

            .and()
            .authorizeHttpRequests()
            .requestMatchers(
            "/clients"
            ).hasAuthority("ROLE_CLIENT")

            .and()
            .authorizeHttpRequests()
            .requestMatchers(
                    "/admin/path//create",
                    "/admin/**"
            ).hasAuthority("ROLE_ADMIN")

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
            .logoutSuccessUrl("/login?logout")
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .permitAll()
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID")

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
        PasswordEncoder passwordEncoder(){
            return new BCryptPasswordEncoder();
        }

        @Bean
        AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }
}