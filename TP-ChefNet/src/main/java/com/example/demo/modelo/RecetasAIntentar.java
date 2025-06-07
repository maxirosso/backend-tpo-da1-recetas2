package com.example.demo.modelo;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "recetas_a_intentar")
@IdClass(RecetasAIntentarId.class)
public class RecetasAIntentar {

    @Id
    @Column(name = "idReceta")
    private Integer idReceta;

    @Id
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "completada", columnDefinition = "bit default 0")
    private Boolean completada = false;

    @Column(name = "fechaCompletada")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCompletada;

    @Column(name = "fechaAgregada", columnDefinition = "datetime default getdate()")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAgregada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idReceta", insertable = false, updatable = false)
    private Recetas receta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario", insertable = false, updatable = false)
    private Usuarios usuario;

    // Constructores
    public RecetasAIntentar() {}

    public RecetasAIntentar(Integer idReceta, Integer idUsuario) {
        this.idReceta = idReceta;
        this.idUsuario = idUsuario;
        this.completada = false;
        this.fechaAgregada = new Date();
    }

    public RecetasAIntentar(Integer idReceta, Integer idUsuario, Boolean completada, Date fechaCompletada) {
        this.idReceta = idReceta;
        this.idUsuario = idUsuario;
        this.completada = completada;
        this.fechaCompletada = fechaCompletada;
        this.fechaAgregada = new Date();
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

    public Boolean getCompletada() {
        return completada;
    }

    public void setCompletada(Boolean completada) {
        this.completada = completada;
        if (completada != null && completada) {
            this.fechaCompletada = new Date();
        } else {
            this.fechaCompletada = null;
        }
    }

    public Date getFechaCompletada() {
        return fechaCompletada;
    }

    public void setFechaCompletada(Date fechaCompletada) {
        this.fechaCompletada = fechaCompletada;
    }

    public Date getFechaAgregada() {
        return fechaAgregada;
    }

    public void setFechaAgregada(Date fechaAgregada) {
        this.fechaAgregada = fechaAgregada;
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

    @Override
    public String toString() {
        return "RecetasAIntentar{" +
                "idReceta=" + idReceta +
                ", idUsuario=" + idUsuario +
                ", completada=" + completada +
                ", fechaCompletada=" + fechaCompletada +
                ", fechaAgregada=" + fechaAgregada +
                '}';
    }
} 