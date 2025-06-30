package com.example.demo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "pasos")
public class Pasos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idPaso")
	private Integer idPaso;
	@ManyToOne
	@JoinColumn(name = "idReceta")
	private Recetas idReceta;
	@Column(name = "nroPaso")
	private int nroPaso;
	@Column(name = "texto")
	private String texto;
	
	public Pasos() {}
	
	public Pasos(Integer idPaso, Recetas idReceta, int nroPaso, String texto) {
		this.idPaso = idPaso;
		this.idReceta = idReceta;
		this.nroPaso = nroPaso;
		this.texto = texto;
	}

	public Integer getIdPaso() {
		return idPaso;
	}

	public void setIdPaso(Integer idPaso) {
		this.idPaso = idPaso;
	}

	public Recetas getIdReceta() {
		return idReceta;
	}

	public void setIdReceta(Recetas idReceta) {
		this.idReceta = idReceta;
	}

	public int getNroPaso() {
		return nroPaso;
	}

	public void setNroPaso(int nroPaso) {
		this.nroPaso = nroPaso;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@Override
	public String toString() {
		return "Pasos [idPaso=" + idPaso + ", idReceta=" + idReceta + ", nroPaso=" + nroPaso + ", texto=" + texto + "]";
	}
	
	

}
