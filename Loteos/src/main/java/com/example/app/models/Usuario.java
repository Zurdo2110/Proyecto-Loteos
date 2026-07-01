package com.example.app.models;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol; // Acá guardaremos "ROLE_ADMIN" o "ROLE_CLIENTE"

    // Relación: Un usuario cliente puede estar asignado a UN loteo para verlo
    @ManyToOne
    @JoinColumn(name = "id_loteo")
    private Loteo loteoAsignado;

    // Constructores
    public Usuario() {}

    public Usuario(String username, String password, String rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Loteo getLoteoAsignado() { return loteoAsignado; }
    public void setLoteoAsignado(Loteo loteoAsignado) { this.loteoAsignado = loteoAsignado; }    
}
