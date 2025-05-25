package com.example.demo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sedes")
public class Sedes {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idSede")
	private Integer idSede;
	@Column(name = "nombreSede")
	private String nombreSede;
	@Column(name = "direccionSede")
	private String direccionSede;
	@Column(name = "telefonoSede")
	private String telefonoSede;
	@Column(name = "mailSede")
	private String mailSede;
	@Column(name = "whatsApp")
	private String whatsApp;
	@Column(name = "tipoBonificacion")
	private String tipoBonificacion;
	@Column(name = "bonificacionCursos")
	private float bonificacionCursos;
	@Column(name = "tipoPromocion")
	private String tipoPromocion;
	@Column(name = "promocionCursos")
	private float promocionCursos;
	
	public Sedes() {}
	
	public Sedes(Integer idSede, String nombreSede, String direccionSede, String telefonoSede, String mailSede, String whatsApp, String tipoBonificacion, float bonificacionCursos, String tipoPromocion, float promocionCursos) {
		this.idSede = idSede;
		this.nombreSede = nombreSede;
		this.direccionSede = direccionSede;
		this.telefonoSede = telefonoSede;
		this.mailSede = mailSede;
		this.whatsApp = whatsApp;
		this.tipoBonificacion = tipoBonificacion;
		this.bonificacionCursos = bonificacionCursos;
		this.tipoPromocion = tipoPromocion;
		this.promocionCursos = promocionCursos;
		
	}

	public Integer getIdSede() {
		return idSede;
	}

	public void setIdSede(Integer idSede) {
		this.idSede = idSede;
	}

	public String getNombreSede() {
		return nombreSede;
	}

	public void setNombreSede(String nombreSede) {
		this.nombreSede = nombreSede;
	}

	public String getDireccionSede() {
		return direccionSede;
	}

	public void setDireccionSede(String direccionSede) {
		this.direccionSede = direccionSede;
	}

	public String getTelefonoSede() {
		return telefonoSede;
	}

	public void setTelefonoSede(String telefonoSede) {
		this.telefonoSede = telefonoSede;
	}

	public String getMailSede() {
		return mailSede;
	}

	public void setMailSede(String mailSede) {
		this.mailSede = mailSede;
	}

	public String getWhatsApp() {
		return whatsApp;
	}

	public void setWhatsApp(String whatsApp) {
		this.whatsApp = whatsApp;
	}

	public String getTipoBonificacion() {
		return tipoBonificacion;
	}

	public void setTipoBonificacion(String tipoBonificacion) {
		this.tipoBonificacion = tipoBonificacion;
	}

	public float getBonificacionCursos() {
		return bonificacionCursos;
	}

	public void setBonificacionCursos(float bonificacionCursos) {
		this.bonificacionCursos = bonificacionCursos;
	}

	public String getTipoPromocion() {
		return tipoPromocion;
	}

	public void setTipoPromocion(String tipoPromocion) {
		this.tipoPromocion = tipoPromocion;
	}

	public float getPromocionCursos() {
		return promocionCursos;
	}

	public void setPromocionCursos(float promocionCursos) {
		this.promocionCursos = promocionCursos;
	}

	@Override
	public String toString() {
		return "Sedes [idSede=" + idSede + ", nombreSede=" + nombreSede + ", direccionSede=" + direccionSede
				+ ", telefonoSede=" + telefonoSede + ", mailSede=" + mailSede + ", whatsApp=" + whatsApp
				+ ", tipoBonificacion=" + tipoBonificacion + ", bonificacionCursos=" + bonificacionCursos
				+ ", tipoPromocion=" + tipoPromocion + ", promocionCursos=" + promocionCursos + "]";
	}
	
	
}
