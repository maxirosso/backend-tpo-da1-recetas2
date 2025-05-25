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
@Table(name = "tiposReceta")
public class TiposReceta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idTipo")
	private Integer idTipo;
	@Column(name = "descripcion")
	private String descripcion;
	@OneToMany(mappedBy = "idTipo")
	private List<Recetas> recetas;

	
	public TiposReceta() {}
	
	public TiposReceta(Integer idTipo, String descripcion) {
		this.idTipo = idTipo;
		this.descripcion = descripcion;
	}

	public Integer getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(Integer idTipo) {
		this.idTipo = idTipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return "TiposReceta [idTipo=" + idTipo + ", descripcion=" + descripcion + "]";
	}
	
	
}
