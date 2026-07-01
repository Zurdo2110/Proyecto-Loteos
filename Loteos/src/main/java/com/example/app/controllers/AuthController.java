package com.example.app.controllers; // Dejá tu paquete original

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        
        // Esto sirve para mostrar carteles de error si pone mal la clave
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos.");
        }
        // Esto avisa si cerró sesión correctamente
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión exitosamente.");
        }
        
        return "login"; // Va a buscar la vista login.mustache
    }

    @Autowired
    private com.example.app.repositories.UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String enrutadorPrincipal(org.springframework.security.core.Authentication authentication) {
        String username = authentication.getName();
        com.example.app.models.Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

        // --- DETECTOR DE MENTIRAS EN CONSOLA ---
        System.out.println("=== LOGIN EXITOSO ===");
        System.out.println("Usuario logueado: " + username);
        System.out.println("Rol exacto en la DB: '" + usuario.getRol() + "'");

        // Usamos trim() para borrar espacios accidentales y equalsIgnoreCase para ignorar mayúsculas/minúsculas
        if (usuario != null && usuario.getRol().trim().equalsIgnoreCase("CLIENTE")) {
            Integer idLoteo = usuario.getLoteoAsignado().getIdLoteo();
            System.out.println("Destino: Redirigiendo a pantalla de CLIENTE (Loteo " + idLoteo + ")");
            return "redirect:/cliente/loteos/" + idLoteo + "/lotes";
        }
        
        System.out.println("Destino: Redirigiendo a panel general de ADMIN");
        return "redirect:/loteos";
    }
    
}

