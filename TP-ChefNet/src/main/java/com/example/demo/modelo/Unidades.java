package com.example.demo.modelo;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "unidades")
public class Unidades {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idUnidad")
	private Integer idUnidad;
	@Column(name = "descripcion")
	private String descripcion;
	@OneToMany(mappedBy = "idUnidadDestino")
	private List<Conversiones> conversionesDestino;
	@OneToMany(mappedBy = "idUnidadOrigen")
	private List<Conversiones> conversionesOrigen;


	
	public Unidades() {}
	
	public Unidades(Integer idUnidad, String descripcion) {
		this.idUnidad = idUnidad;
		this.descripcion = descripcion;
	}

	public Integer getIdUnidad() {
		return idUnidad;
	}

	public void setIdUnidad(Integer idUnidad) {
		this.idUnidad = idUnidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return "Unidades [idUnidad=" + idUnidad + ", descripcion=" + descripcion + "]";
	}

}
