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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversiones")
public class Conversiones {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idConversion")
	private Integer idConversion;
	@ManyToOne
	@JoinColumn(name = "idUnidadOrigen")
	private Unidades idUnidadOrigen;
	@ManyToOne
	@JoinColumn(name = "idUnidadDestino")
	private Unidades idUnidadDestino;
	@Column(name = "factorConversiones")
	private float factorConversiones;

	public Conversiones() {}
	
	public Conversiones(Integer idConversion, Unidades idUnidadOrigen, Unidades idUnidadDestino, float factorConversiones) {
		this.idConversion = idConversion;
		this.idUnidadOrigen = idUnidadOrigen;
		this.idUnidadDestino = idUnidadDestino;
		this.factorConversiones = factorConversiones;
	}

	public Integer getIdConversion() {
		return idConversion;
	}

	public void setIdConversion(Integer idConversion) {
		this.idConversion = idConversion;
	}

	public Unidades getIdUnidadOrigen() {
		return idUnidadOrigen;
	}

	public void setIdUnidadOrigen(Unidades idUnidadOrigen) {
		this.idUnidadOrigen = idUnidadOrigen;
	}

	public Unidades getIdUnidadDestino() {
		return idUnidadDestino;
	}

	public void setIdUnidadDestino(Unidades idUnidadDestino) {
		this.idUnidadDestino = idUnidadDestino;
	}

	public float getFactorConversiones() {
		return factorConversiones;
	}

	public void setFactorConversiones(float factorConversiones) {
		this.factorConversiones = factorConversiones;
	}

	@Override
	public String toString() {
		return "Conversiones [idConversion=" + idConversion + ", idUnidadOrigen=" + idUnidadOrigen
				+ ", idUnidadDestino=" + idUnidadDestino + ", factorConversiones=" + factorConversiones + "]";
	}

	
	
}
