package com.example.demo.datos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Alumnos;
import com.example.demo.modelo.CronogramaCursos;
import com.example.demo.modelo.Cursos;
import com.example.demo.modelo.Inscripcion;
import com.example.demo.modelo.Recetas;

@Repository
public class CursosDAO {
	@Autowired
	CursosRepository cursosRepository;
	@Autowired
	CronogramaCursosRepository cronogramaCursosRepository;
	@Autowired
	AlumnosRepository alumnosRepository;
	@Autowired
	InscripcionRepository inscripcionRepository;
	
	public List<Cursos> getAllCursos(CursosRepository cursosRepository){
		return cursosRepository.findAll();
	}
	
    public void save(Cursos cursos) {
    	cursosRepository.save(cursos);
    }
	
	public void delete(Cursos cursos) {
		cursosRepository.delete(cursos);;
	}
	

    public void inscribirAlumnoACurso(int idAlumno, int idCronograma) {

        CronogramaCursos cronograma = cronogramaCursosRepository.findById(idCronograma)
            .orElseThrow(() -> new IllegalStateException("Cronograma no encontrado"));

        if (cronograma.getVacantesDisponibles() <= 0) {
            throw new IllegalStateException("No hay vacantes disponibles");
        }

        Alumnos alumno = alumnosRepository.findById(idAlumno)
            .orElseThrow(() -> new IllegalStateException("Alumno no encontrado"));

        BigDecimal montoCurso = cronograma.getIdCurso().getPrecio();
        if (alumno.getCuentaCorriente().compareTo(montoCurso) < 0) {
            throw new IllegalStateException("Fondos insuficientes");
        }

        alumno.setCuentaCorriente(alumno.getCuentaCorriente().subtract(montoCurso));
        alumnosRepository.save(alumno);

        // Crear inscripción
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setAlumno(alumno);
        inscripcion.setCurso(cronograma.getIdCurso());
        inscripcion.setFechaInscripcion(new Date());        
        inscripcion.setEstadoInscripcion("Inscrito");
        inscripcion.setMonto(montoCurso);
        inscripcion.setEstadoPago("Pagado");

        inscripcionRepository.save(inscripcion);

        // Reducir vacantes
        cronograma.setVacantesDisponibles(cronograma.getVacantesDisponibles() - 1);
        cronogramaCursosRepository.save(cronograma);
    }
    
    public List<Cursos> obtenerCursosPorAlumno(int idAlumno) {
        List<Inscripcion> inscripciones = inscripcionRepository.findByAlumno_IdAlumno(idAlumno);

        return inscripciones.stream().map(insc -> {
            CronogramaCursos cronograma = insc.getCronograma();  
            Cursos curso = cronograma.getIdCurso(); 

            Cursos cursos = new Cursos();
            cursos.setDescripcion(curso.getDescripcion());
            cursos.setContenidos(curso.getContenidos());
            cursos.setRequerimientos(curso.getRequerimientos());
            cursos.setDuracion(curso.getDuracion());
            cursos.setPrecio(curso.getPrecio());
            cursos.setModalidad(curso.getModalidad());

            LocalDateTime fechaInicio = cronograma.getFechaInicio();
            LocalDateTime fechaFin = cronograma.getFechaFin();

            String estadoInscripcion = insc.getEstadoInscripcion(); 
            String estadoPago = insc.getEstadoPago();

            BigDecimal monto = insc.getMonto(); 
            return cursos;
        }).collect(Collectors.toList());
    }


}
