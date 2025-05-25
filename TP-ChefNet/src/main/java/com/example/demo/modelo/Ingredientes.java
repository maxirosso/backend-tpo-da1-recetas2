package com.example.demo.modelo;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredientes")
public class Ingredientes {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idIngrediente")
	private Integer idIngrediente;
	@Column(name = "nombre")
	private String nombre;
	
    @ManyToOne
    @JoinColumn(name = "id_receta")
    private Recetas receta;
    
    @Column(name = "cantidad")
    private double cantidad;

    @Column(name = "unidadMedida")
    private String unidadMedida;
    
    @OneToMany(mappedBy = "ingrediente")
    private List<Utilizados> utilizados;

	public Ingredientes() {}
	
	public Ingredientes(Integer idIngrediente, String nombre) {
		this.idIngrediente = idIngrediente;
		this.nombre = nombre;
	}

	public Integer getIdIngrediente() {
		return idIngrediente;
	}

	public void setIdIngrediente(Integer idIngrediente) {
		this.idIngrediente = idIngrediente;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Recetas getReceta() {
		return receta;
	}

	public void setReceta(Recetas receta) {
		this.receta = receta;
	}
	
	public double getCantidad() {
	    return cantidad;
	}

	public void setCantidad(double cantidad) {
	    this.cantidad = cantidad;
	}

	public String getUnidadMedida() {
	    return unidadMedida;
	}

	public void setUnidadMedida(String unidadMedida) {
	    this.unidadMedida = unidadMedida;
	}

	
	public Ingredientes clonar() {
	    Ingredientes clon = new Ingredientes();
	    clon.setNombre(this.getNombre());
	    clon.setCantidad(this.getCantidad());
	    clon.setUnidadMedida(this.getUnidadMedida());
	    return clon;
	}


	@Override
	public String toString() {
		return "Ingredientes [idIngrediente=" + idIngrediente + ", nombre=" + nombre + "]";
	}
	
	

}
