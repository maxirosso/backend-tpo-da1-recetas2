package com.example.demo.modelo;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "recetas_guardadas")
@IdClass(RecetasGuardadasId.class)
public class RecetasGuardadas {

    @Id
    @Column(name = "idReceta")
    private Integer idReceta;

    @Id
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "fechaGuardada", columnDefinition = "datetime default getdate()")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGuardada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idReceta", insertable = false, updatable = false)
    private Recetas receta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario", insertable = false, updatable = false)
    private Usuarios usuario;

    // Constructores
    public RecetasGuardadas() {}

    public RecetasGuardadas(Integer idReceta, Integer idUsuario) {
        this.idReceta = idReceta;
        this.idUsuario = idUsuario;
        this.fechaGuardada = new Date();
    }

    // Getters y Setters
    public Integer getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(Integer idReceta) {
        this.idReceta = idReceta;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getFechaGuardada() {
        return fechaGuardada;
    }

    public void setFechaGuardada(Date fechaGuardada) {
        this.fechaGuardada = fechaGuardada;
    }

    public Recetas getReceta() {
        return receta;
    }

    public void setReceta(Recetas receta) {
        this.receta = receta;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }
} 