package com.example.app.repositories;

import com.example.app.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Este método es crucial: Spring Security lo va a usar para buscar quién se está logueando
    Optional<Usuario> findByUsername(String username);
}