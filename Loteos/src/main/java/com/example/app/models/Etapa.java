package com.example.app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "etapas")
public class Etapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEtapa;

    @Column(nullable = false, length = 100)
    private String nombre; // Ej: "Fase 1", "Sector Norte"

    @Column(length = 50)
    private String expedienteMunicipal; // Para darle vida propia administrativa

    // Relación N a 1: Muchas etapas pertenecen a un Loteo
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loteo", nullable = false)
    private Loteo loteo;

    public Etapa() {}

    // --- Getters y Setters ---
    public Integer getIdEtapa() { return idEtapa; }
    public void setIdEtapa(Integer idEtapa) { this.idEtapa = idEtapa; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getExpedienteMunicipal() { return expedienteMunicipal; }
    public void setExpedienteMunicipal(String expedienteMunicipal) { this.expedienteMunicipal = expedienteMunicipal; }

    public Loteo getLoteo() { return loteo; }
    public void setLoteo(Loteo loteo) { this.loteo = loteo; }
}
