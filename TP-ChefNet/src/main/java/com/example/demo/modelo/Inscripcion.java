package com.example.demo.modelo;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.*;


@Entity
@Table(name = "inscripciones") 
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idInscripcion")
    private int idInscripcion;

    @ManyToOne
    @JoinColumn(name = "idAlumno", nullable = false)
    private Alumnos alumno;
    
    @Column(name = "idAlumno", insertable = false, updatable = false)
    private Integer idAlumno;
    
    @ManyToOne
    @JoinColumn(name = "idCronograma", nullable = false)
    private CronogramaCursos cronograma;

    @Column(name = "idCronograma", insertable = false, updatable = false)
    private Integer idCronograma;

    @ManyToOne
    @JoinColumn(name = "idCurso", nullable = false)
    private Cursos curso; 

    @Column(name = "idCurso", insertable = false, updatable = false)
    private Integer idCurso;

    @Column(name = "fechaInscripcion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInscripcion; 

    @Column(name = "estadoInscripcion", nullable = false)
    private String estadoInscripcion; //Inscrito, Cancelado, etc
    
    @Column(name = "estadoPago", nullable = false)
    private String estadoPago; //pendiente, pagado, etc
    
    @Column(name = "monto", nullable = false)
    private BigDecimal monto;
    


    public Inscripcion() {}

    public Inscripcion(Alumnos alumno, Cursos curso, CronogramaCursos cronograma, Date fechaInscripcion, String estadoInscripcion, String estadoPago, BigDecimal monto) {
		this.alumno = alumno;
		this.curso = curso;
		this.cronograma = cronograma;
		this.fechaInscripcion = fechaInscripcion;
		this.estadoInscripcion = estadoInscripcion;
		this.estadoPago = estadoPago;
		this.monto = monto;
    }

    public int getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public Alumnos getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumnos alumno) {
        this.alumno = alumno;
    }

    public Cursos getCurso() {
        return curso;
    }

    public void setCurso(Cursos curso) {
        this.curso = curso;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getEstadoInscripcion() {
        return estadoInscripcion;
    }

    public void setEstadoInscripcion(String estadoInscripcion) {
        this.estadoInscripcion = estadoInscripcion;
    }
    
	public BigDecimal getMonto() {
	    return monto;
	}
	
	public void setMonto(BigDecimal monto) {
	    this.monto = monto;
	}

    public CronogramaCursos getCronograma() {
		return cronograma;
	}

	public void setCronograma(CronogramaCursos cronograma) {
		this.cronograma = cronograma;
	}

	public String getEstadoPago() {
		return estadoPago;
	}

	public void setEstadoPago(String estadoPago) {
		this.estadoPago = estadoPago;
	}

	public Integer getIdAlumno() {
		return idAlumno;
	}

	public void setIdAlumno(Integer idAlumno) {
		this.idAlumno = idAlumno;
	}

	public Integer getIdCronograma() {
		return idCronograma;
	}

	public void setIdCronograma(Integer idCronograma) {
		this.idCronograma = idCronograma;
	}

	public Integer getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Integer idCurso) {
		this.idCurso = idCurso;
	}

	@Override
    public String toString() {
        return "Inscripcion{" +
                "idInscripcion=" + idInscripcion +
                ", alumno=" + alumno.getIdAlumno() + 
                ", curso=" + curso.getDescripcion() + 
                ", fechaInscripcion=" + fechaInscripcion +
                ", estadoInscripcion='" + estadoInscripcion + '\'' +
                '}';
    }
}
