package com.example.demo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "fotos")
public class Fotos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idfoto")
	private Integer idfoto;
	@ManyToOne
	@JoinColumn(name = "idReceta")
	private Recetas idReceta;
	@Column(name = "urlFoto")
	private String urlFoto;
	@Column(name = "extension")
	private String extension;
	
	public Fotos() {}
	
	public Fotos(Integer idfoto, Recetas idReceta, String urlFoto, String extension) {
		this.idfoto = idfoto;
		this.idReceta = idReceta;
		this.urlFoto = urlFoto;
		this.extension = extension;
	}

	public Integer getIdfoto() {
		return idfoto;
	}

	public void setIdfoto(Integer idfoto) {
		this.idfoto = idfoto;
	}

	public Recetas getIdReceta() {
		return idReceta;
	}

	public void setIdReceta(Recetas idReceta) {
		this.idReceta = idReceta;
	}

	public String getUrlFoto() {
		return urlFoto;
	}

	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public String toString() {
		return "Fotos [idfoto=" + idfoto + ", idReceta=" + idReceta + ", urlFoto=" + urlFoto + ", extension="
				+ extension + "]";
	}
	
	

}
