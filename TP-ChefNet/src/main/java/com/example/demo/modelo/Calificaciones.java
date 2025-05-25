package com.example.demo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "calificaciones")
public class Calificaciones {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idCalificacion")
	private Integer idCalificacion;
	@OneToOne
	@JoinTable(name = "usuarios")
	private Usuarios idusuario;
	@OneToOne
	@JoinTable(name = "recetas")
	private Recetas idReceta;
	@Column(name = "calificacion")
	private int calificacion;
	@Column(name = "comentarios")
	private String comentarios;
    @Column(name = "autorizado")
    private boolean autorizado = false;
	
	public Calificaciones() {}
	
	public Calificaciones(Integer idCalificacion, Usuarios idusuario, Recetas idReceta, int calificacion, String comentarios, boolean autorizado) {
		this.idCalificacion = idCalificacion;
		this.idusuario = idusuario;
		this.idReceta = idReceta;
		this.calificacion = calificacion;
		this.comentarios = comentarios;
        this.autorizado = autorizado;

	}

	public Integer getIdCalificacion() {
		return idCalificacion;
	}

	public void setIdCalificacion(Integer idCalificacion) {
		this.idCalificacion = idCalificacion;
	}

	public Usuarios getIdusuario() {
		return idusuario;
	}

	public void setIdusuario(Usuarios idusuario) {
		this.idusuario = idusuario;
	}

	public Recetas getIdReceta() {
		return idReceta;
	}

	public void setIdReceta(Recetas idReceta) {
		this.idReceta = idReceta;
	}

	public int getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(int calificacion) {
		this.calificacion = calificacion;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}
	
    public boolean isAutorizado() {
        return autorizado;
    }

    public void setAutorizado(boolean autorizado) {
        this.autorizado = autorizado;
    }

	@Override
	public String toString() {
		return "Calificaciones [idCalificacion=" + idCalificacion + ", idusuario=" + idusuario + ", idReceta="
				+ idReceta + ", calificacion=" + calificacion + ", comentarios=" + comentarios + ", autorizado=" + autorizado + "]";
	}
	
	
}
