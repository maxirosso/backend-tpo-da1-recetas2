package com.example.demo.modelo;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cronogramaCursos")
public class CronogramaCursos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idCronograma")
	private Integer idCronograma;
    @OneToOne  
    @JoinColumn(name = "idSede") 
	private Sedes idSede;
	@OneToOne
	@JoinColumn(name = "idCurso") 
	private Cursos idCurso;
	@Column(name = "fechaInicio")
	private LocalDateTime fechaInicio;
	@Column(name = "fechaFin")
	private LocalDateTime fechaFin;
	@Column(name = "vacantesDisponibles")
	private int vacantesDisponibles;
	
	@OneToMany(mappedBy = "cronogramaCursos")
	@JsonIgnore
	private List<Alumnos> alumnos;
	
	public CronogramaCursos() {}
	
	public CronogramaCursos(Integer idCronograma, Sedes idSede, Cursos idCurso, LocalDateTime fechaInicio, LocalDateTime fechaFin, int vacantesDisponibles) {
		this.idCronograma = idCronograma;
		this.idSede = idSede;
		this.idCurso = idCurso;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.vacantesDisponibles = vacantesDisponibles;
		
	}

	public Integer getIdCronograma() {
		return idCronograma;
	}

	public void setIdCronograma(Integer idCronograma) {
		this.idCronograma = idCronograma;
	}

	public Sedes getIdSede() {
		return idSede;
	}

	public void setIdSede(Sedes idSede) {
		this.idSede = idSede;
	}

	public Cursos getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Cursos idCurso) {
		this.idCurso = idCurso;
	}

	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDateTime fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDateTime fechaFin) {
		this.fechaFin = fechaFin;
	}

	public int getVacantesDisponibles() {
		return vacantesDisponibles;
	}

	public void setVacantesDisponibles(int vacantesDisponibles) {
		this.vacantesDisponibles = vacantesDisponibles;
	}

	@Override
	public String toString() {
		return "CronogramaCursos [idCronograma=" + idCronograma + ", idSede=" + idSede + ", idCurso=" + idCurso
				+ ", fechaInicio=" + fechaInicio + ", fechaFin=" + fechaFin + ", vacantesDisponibles="
				+ vacantesDisponibles + "]";
	}
	
	

}
