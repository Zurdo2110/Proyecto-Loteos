package com.example.app.services; // ¡Ajustá tu paquete!

import com.example.app.models.Usuario;
import com.example.app.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos al usuario en PostgreSQL
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Lo convertimos al formato que Spring Security entiende
        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword()) 
                .roles(usuario.getRol()) // Acá leerá "ADMIN" o "CLIENTE"
                .build();
    }
}