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
@Table(name = "multimedia")
public class Multimedia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idContenido")
	private Integer idContenido;
	@ManyToOne
	@JoinColumn(name = "idPaso")
	private Pasos idPaso;
	@Column(name = "tipoContenido")
	private String tipoContenido;
	@Column(name = "extension")
	private String extension;
	@Column(name = "urlContenido")
	private String urlContenido;
    @ManyToOne
    private Recetas receta; 
	
	public Multimedia() {}
	
	public Multimedia(Integer idContenido, Pasos idPaso, String tipoContenido, String extension, String urlContenido) {
		this.idContenido = idContenido;
		this.idPaso = idPaso;
		this.tipoContenido = tipoContenido;
		this.extension = extension;
		this.urlContenido = urlContenido;
	}

	public Integer getIdContenido() {
		return idContenido;
	}

	public void setIdContenido(Integer idContenido) {
		this.idContenido = idContenido;
	}

	public Pasos getIdPaso() {
		return idPaso;
	}

	public void setIdPaso(Pasos idPaso) {
		this.idPaso = idPaso;
	}

	public String getTipoContenido() {
		return tipoContenido;
	}

	public void setTipoContenido(String tipoContenido) {
		this.tipoContenido = tipoContenido;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getUrlContenido() {
		return urlContenido;
	}

	public void setUrlContenido(String urlContenido) {
		this.urlContenido = urlContenido;
	}

	public Recetas getReceta() {
		return receta;
	}

	public void setReceta(Recetas receta) {
		this.receta = receta;
	}

	@Override
	public String toString() {
		return "Multimedia [idContenido=" + idContenido + ", idPaso=" + idPaso + ", tipoContenido=" + tipoContenido
				+ ", extension=" + extension + ", urlContenido=" + urlContenido + "]";
	}
	
	

}
