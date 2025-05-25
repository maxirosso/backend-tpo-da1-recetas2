package com.example.demo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "utilizados")
public class Utilizados {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idUtilizado")
	private Integer idUtilizado;
	@ManyToOne
	@JoinColumn(name = "idReceta")
	private Recetas receta;
	@ManyToOne
	@JoinColumn(name = "idIngrediente")
	private Ingredientes ingrediente;
	@Column(name = "cantidad")
	private int cantidad;
	@ManyToOne
	@JoinColumn(name = "idUnidad") 
	private Unidades idUnidad; 
	@Column(name = "observaciones")
	private String observaciones;
	
	public Utilizados() {}
	
	public Utilizados(Integer idUtilizado, Recetas receta, Ingredientes ingrediente, int cantidad, Unidades idUnidad, String observaciones) {
		this.idUtilizado = idUtilizado;
		this.receta = receta;
		this.ingrediente = ingrediente;
		this.cantidad = cantidad;
		this.idUnidad = idUnidad;
		this.observaciones = observaciones;
	}

	public Integer getIdUtilizado() {
		return idUtilizado;
	}

	public void setIdUtilizado(Integer idUtilizado) {
		this.idUtilizado = idUtilizado;
	}

	public Recetas getReceta() {
		return receta;
	}

	public void setReceta(Recetas receta) {
		this.receta = receta;
	}

	public Ingredientes getIngrediente() {
		return ingrediente;
	}

	public void setIngrediente(Ingredientes ingrediente) {
		this.ingrediente = ingrediente;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public Unidades getIdUnidad() {
		return idUnidad;
	}

	public void setIdUnidad(Unidades idUnidad) {
		this.idUnidad = idUnidad;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	@Override
	public String toString() {
		return "Utilizados [idUtilizado=" + idUtilizado + ", Receta=" + receta + ", idIngrediente=" + ingrediente
				+ ", cantidad=" + cantidad + ", idUnidad=" + idUnidad + ", observaciones=" + observaciones + "]";
	}
	
	

}
