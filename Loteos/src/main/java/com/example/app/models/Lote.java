package com.example.app.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore; 

@Entity
@Table(name = "lotes")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLote;

    // Limita la columna a 12 caracteres máximo en la Base de Datos
    @Column(name = "numero_cuenta", nullable = false, length = 12)
    private String numeroCuenta;

    // Limita la columna a 16 caracteres máximo en la Base de Datos
    @Column(length = 16)
    private String nomenclatura;

    @Column(columnDefinition = "TEXT")
    private String titular;

    // NUEVO: Designación Oficial (Ej: Lote 1, Manzana A)
    @Column(name = "designacion_oficial", length = 150)
    private String designacionOficial;

    @Column (name = "matricula", length = 10)
    private String matricula;

    // Superficie total del terreno
    private Double superficie;

    // NUEVO: Superficie construida/cubierta
    @Column(name = "superficie_cubierta")
    private Double superficieCubierta;

    @Column(name = "cuentaEmos", length = 7)
    private String cuentaEmos;

    @Column(name = "cuentaMuni", length = 9)
    private String cuentaMuni;

    @Column(name = "domicilio", length = 200)
    private String domicilio;

    @Column(name = "manzana", length = 5)
    private String manzana;

    @Column(name = "numeroLote", length = 5)
    private String numeroLote;

    // --- ATRIBUTOS VIEJOS (Los mantenemos por ahora para no romper el Controlador) ---
    @Column(length = 50)
    private String estado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // Relación N a 1
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loteo", nullable = false)
    private Loteo loteo;

    // NUEVA RELACIÓN OPCIONAL (nullable = true)
    // Si el loteo es simple, esto queda en null. Si tiene etapas, se vincula acá.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etapa", nullable = true) 
    private Etapa etapa;

    public Lote() {}

    // --- Getters y Setters ---
    public Integer getIdLote() { return idLote; }
    public void setIdLote(Integer idLote) { this.idLote = idLote; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public String getNomenclatura() { return nomenclatura; }
    public void setNomenclatura(String nomenclatura) { this.nomenclatura = nomenclatura; }

    public String getTitular() { return titular != null ? titular : "Sin titular asignado"; }
    public void setTitular(String titular) { this.titular = titular; }

    public String getDesignacionOficial() { return designacionOficial != null ? designacionOficial : "Sin designar"; }
    public void setDesignacionOficial(String designacionOficial) { this.designacionOficial = designacionOficial; }

    public String getMatricula() { return matricula != null ? matricula : "-"; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public Double getSuperficie() { return superficie != null ? superficie : 0.0; }
    public void setSuperficie(Double superficie) { this.superficie = superficie; }

    public Double getSuperficieCubierta() { return superficieCubierta != null ? superficieCubierta : 0.0; }
    public void setSuperficieCubierta(Double superficieCubierta) { this.superficieCubierta = superficieCubierta; }

    public String getObservaciones() { return observaciones != null ? observaciones : "-"; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Loteo getLoteo() { return loteo; }
    public void setLoteo(Loteo loteo) { this.loteo = loteo; }

    public Etapa getEtapa() { return etapa; }
    public void setEtapa(Etapa etapa) { this.etapa = etapa; }

    public String getCuentaEmos() { return cuentaEmos; }
    public void setCuentaEmos(String cuentaEmos) { this.cuentaEmos = cuentaEmos; }

    public String getCuentaMuni() { return cuentaMuni; }
    public void setCuentaMuni(String cuentaMuni) { this.cuentaMuni = cuentaMuni; }     

    public String getDomicilio() { return domicilio != null ? domicilio : "-"; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getManzana() { return manzana; }
    public void setManzana(String manzana) { this.manzana = manzana; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
}