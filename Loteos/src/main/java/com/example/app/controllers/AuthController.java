package com.example.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        
        // Si Spring Security detecta credenciales malas, nos manda el parámetro "error"
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos.");
        }
        
        // Si el usuario acaba de tocar el botón rojo de salir, nos manda "logout"
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión correctamente.");
        }
        
        return "login";
    }
}