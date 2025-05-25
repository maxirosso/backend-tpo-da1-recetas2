package com.example.demo.modelo;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cursos")
public class Cursos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idCurso")
	private Integer idCurso;
	@Column(name = "descripcion")
	private String descripcion;
	@Column(name = "contenidos")
	private String contenidos;
	@Column(name = "requerimientos")
	private String requerimientos;
	@Column(name = "duracion")
	private int duracion;
	@Column(name = "precio")
	private BigDecimal precio;
	@Column(name = "modalidad")
	private String modalidad;
	
	public Cursos() {}
	
	public Cursos(Integer idCurso, String descripcion, String contenidos, String requerimientos, int duracion, BigDecimal precio, String modalidad) {
		this.idCurso = idCurso;
		this.descripcion = descripcion;
		this.contenidos = contenidos;
		this.requerimientos = requerimientos;
		this.duracion = duracion;
		this.precio = precio;
		this.modalidad = modalidad;
		
	}

	public Integer getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Integer idCurso) {
		this.idCurso = idCurso;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getContenidos() {
		return contenidos;
	}

	public void setContenidos(String contenidos) {
		this.contenidos = contenidos;
	}

	public String getRequerimientos() {
		return requerimientos;
	}

	public void setRequerimientos(String requerimientos) {
		this.requerimientos = requerimientos;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

	public String getModalidad() {
		return modalidad;
	}

	public void setModalidad(String modalidad) {
		this.modalidad = modalidad;
	}

	@Override
	public String toString() {
		return "Cursos [idCurso=" + idCurso + ", descripcion=" + descripcion + ", contenidos=" + contenidos
				+ ", requerimientos=" + requerimientos + ", duracion=" + duracion + ", precio=" + precio
				+ ", modalidad=" + modalidad + "]";
	}
	
	

}
