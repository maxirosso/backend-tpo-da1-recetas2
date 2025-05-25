package com.example.demo.modelo;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "asistenciaCursos")
public class AsistenciaCursos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idasistencia")
	private Integer idAsistencia;
	@ManyToOne
	@JoinColumn(name = "idalumno", referencedColumnName = "idalumno") 
	private Alumnos idAlumno;
	@ManyToOne
	@JoinColumn(name = "idcronograma", referencedColumnName = "idcronograma")
	private CronogramaCursos idCronograma;
	@Column(name = "fecha")
	private Date fecha;
	
	public AsistenciaCursos() {}
	
	public AsistenciaCursos(Integer idAsistencia, Alumnos idAlumno, CronogramaCursos idCronograma, Date fecha) {
		this.idAsistencia = idAsistencia;
		this.idAlumno = idAlumno;
		this.idCronograma = idCronograma;
		this.fecha = fecha;		
	}

	public Integer getIdAsistencia() {
		return idAsistencia;
	}

	public void setIdAsistencia(Integer idAsistencia) {
		this.idAsistencia = idAsistencia;
	}

	public Alumnos getIdAlumno() {
		return idAlumno;
	}

	public void setIdAlumno(Alumnos idAlumno) {
		this.idAlumno = idAlumno;
	}

	public CronogramaCursos getIdCronograma() {
		return idCronograma;
	}

	public void setIdCronograma(CronogramaCursos idCronograma) {
		this.idCronograma = idCronograma;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Override
	public String toString() {
		return "AsistenciaCursos [idAsistencia=" + idAsistencia + ", idAlumno=" + idAlumno + ", idCronograma="
				+ idCronograma + ", fecha=" + fecha + "]";
	}

	
}
