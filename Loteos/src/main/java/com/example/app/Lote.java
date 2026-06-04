package com.example.app;

import jakarta.persistence.*;

@Entity
@Table(name = "lotes")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLote;

    @Column(name = "numero_cuenta", nullable = false, length = 50)
    private String numeroCuenta;

    @Column(length = 100)
    private String nomenclatura;

    private Double superficie;

    @Column(length = 50)
    private String estado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column (columnDefinition = "TEXT")
    private String titular;

    // Relación N a 1: Muchos lotes pertenecen a un loteo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loteo", nullable = false)
    private Loteo loteo;

    public Lote() {}

    // --- Getters y Setters ---
    public Integer getIdLote() { return idLote; }
    public void setIdLote(Integer idLote) { this.idLote = idLote; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public String getTitular() { return titular;}
    public void setTitular(String titular) { this.titular = titular; }

    public String getNomenclatura() { return nomenclatura; }
    public void setNomenclatura(String nomenclatura) { this.nomenclatura = nomenclatura; }

    public Double getSuperficie() { return superficie; }
    public void setSuperficie(Double superficie) { this.superficie = superficie; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Loteo getLoteo() { return loteo; }
    public void setLoteo(Loteo loteo) { this.loteo = loteo; }
}