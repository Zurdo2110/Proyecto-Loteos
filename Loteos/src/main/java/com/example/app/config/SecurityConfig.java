package com.example.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Por ahora, exigimos que cualquiera que entre tenga el rol ADMIN (El estudio)
                        .requestMatchers("/loteos/**", "/lotes/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/loteos", true)
                        .permitAll())
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Usuario 1: El administrador del estudio
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("zurdo")
                .password("admin123")
                .roles("ADMIN")
                .build();

        // Usuario 2: El cliente de prueba (Aún no le dimos acceso a ninguna ruta)
        UserDetails cliente = User.withDefaultPasswordEncoder()
                .username("cliente")
                .password("olmos123")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, cliente);
    }
}