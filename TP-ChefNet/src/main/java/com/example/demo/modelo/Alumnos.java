package com.example.demo.modelo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.MapsId;

@Entity
@Table(name = "alumnos")
public class Alumnos {
	@Id
	@Column(name = "idalumno")
	private Integer idAlumno;
	@Column(name = "numerotarjeta")
	private String nroTarjeta;
	@Column(name = "dnifrente")
	private String dniFrente;
	@Column(name = "dnifondo")
	private String dniFondo;
	@Column(name = "tramite")
	private String tramite;
	@Column(name = "cuentacorriente")
	private BigDecimal cuentaCorriente;
	@OneToOne
	@MapsId
	@JoinColumn(name = "idalumno", referencedColumnName = "idUsuario")
	@JsonBackReference
	private Usuarios usuario;
	@ManyToOne
	@JoinColumn(name = "idCronograma", referencedColumnName = "idCronograma")
	@JsonIgnore
	private CronogramaCursos cronogramaCursos;
	
	public Alumnos() {}
	
	public Alumnos(Integer idAlumno, String nroTarjeta, String dniFrente, String dniFondo, String tramite, BigDecimal cuentaCorriente, Usuarios usuario) {
		this.idAlumno = idAlumno;
		this.nroTarjeta = nroTarjeta;
		this.dniFrente = dniFrente;
		this.dniFondo = dniFondo;
		this.tramite = tramite;
		this.cuentaCorriente = cuentaCorriente;
		this.usuario = usuario;
	}

	public Integer getIdAlumno() {
		return idAlumno;
	}

	public void setIdAlumno(Integer idAlumno) {
		this.idAlumno = idAlumno;
	}

	public String getNroTarjeta() {
		return nroTarjeta;
	}

	public void setNroTarjeta(String nroTarjeta) {
		this.nroTarjeta = nroTarjeta;
	}

	public String getDniFrente() {
		return dniFrente;
	}

	public void setDniFrente(String dniFrente) {
		this.dniFrente = dniFrente;
	}

	public String getDniFondo() {
		return dniFondo;
	}

	public void setDniFondo(String dniFondo) {
		this.dniFondo = dniFondo;
	}

	public String getTramite() {
		return tramite;
	}

	public void setTramite(String tramite) {
		this.tramite = tramite;
	}

	public BigDecimal getCuentaCorriente() {
		return cuentaCorriente;
	}

	public void setCuentaCorriente(BigDecimal cuentaCorriente) {
		this.cuentaCorriente = cuentaCorriente;
	}
	
	public Usuarios getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuarios usuario) {
		this.usuario = usuario;
	}

	public CronogramaCursos getCronogramaCursos() {
	    return cronogramaCursos;
	}

	public void setCronogramaCursos(CronogramaCursos cronogramaCursos) {
	    this.cronogramaCursos = cronogramaCursos;
	}

	@Override
	public String toString() {
		return "Alumnos [idAlumno=" + idAlumno + ", nroTarjeta=" + nroTarjeta + ", dniFrente=" + dniFrente
				+ ", dniFondo=" + dniFondo + ", tramite=" + tramite + ", cuentaCorriente=" + cuentaCorriente + "]";
	}
	
	
}
