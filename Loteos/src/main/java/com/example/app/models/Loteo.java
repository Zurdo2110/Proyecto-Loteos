package com.example.app.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "loteos")
public class Loteo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLoteo;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;

    // Relación 1 a N: Un loteo tiene muchos lotes.
    @OneToMany(mappedBy = "loteo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lote> lotes;

    // Constructores
    public Loteo() {}

    public Loteo(String nombre) {
        this.nombre = nombre;
    }

    // --- Getters y Setters ---
    public Integer getIdLoteo() {
        return idLoteo;
    }

    public void setIdLoteo(Integer idLoteo) {
        this.idLoteo = idLoteo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Lote> getLotes() {
        return lotes;
    }

    public void setLotes(List<Lote> lotes) {
        this.lotes = lotes;
    }
}