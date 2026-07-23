package com.example.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;   
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/mapas/**").permitAll()
                .requestMatchers("/error").permitAll()

                .requestMatchers("/").hasAnyRole("ADMIN", "CLIENTE") 
                .requestMatchers("/cliente/**").hasRole("CLIENTE")
                .requestMatchers("/loteos/lotes/**").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers("/lotes/**").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers("/api/lotes/**").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers("/mapas/**").hasAnyRole("ADMIN", "CLIENTE")
                .anyRequest().hasRole("ADMIN")

            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true) // Si entra bien, lo mandamos a un "Enrutador"
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}