package com.example.demo.datos;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Alumnos;
import com.example.demo.modelo.Cursos;
import com.example.demo.modelo.CronogramaCursos;
import com.example.demo.modelo.Inscripcion;

@Repository
public class InscripcionDAO {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private AlumnosRepository alumnosRepository;

    @Autowired
    private CursosRepository cursosRepository;
    
    @Autowired
    private CronogramaCursosRepository cronogramaCursosRepository;

    public List<Inscripcion> getAllInscripciones() {
        return inscripcionRepository.findAll();
    }

    public void save(Inscripcion inscripcion) {
        inscripcionRepository.save(inscripcion);
    }

    public void delete(Inscripcion inscripcion) {
        inscripcionRepository.delete(inscripcion);
    }
    
    public Optional<Inscripcion> findById(int idInscripcion) {
        return inscripcionRepository.findById(idInscripcion);
    }

    // Inscribir un alumno a un curso - ACTUALIZADO para usar cronograma
    public void inscribirAlumno(int idAlumno, int idCronograma) {
        Alumnos alumno = alumnosRepository.findById(idAlumno)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        CronogramaCursos cronograma = cronogramaCursosRepository.findById(idCronograma)
                .orElseThrow(() -> new RuntimeException("Cronograma no encontrado"));

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setAlumno(alumno);
        inscripcion.setCronograma(cronograma);
        // ✅ CORREGIDO: Setear el curso porque la tabla lo requiere
        inscripcion.setCurso(cronograma.getIdCurso());
        inscripcion.setFechaInscripcion(new java.util.Date());
        inscripcion.setEstadoInscripcion("Inscrito"); //puede ser cancelado, etc
        inscripcion.setEstadoPago("Pagado"); //puede ser en proceso, etc
        inscripcion.setMonto(cronograma.getIdCurso().getPrecio());
 
        inscripcionRepository.save(inscripcion);
    }

    public void cancelarInscripcion(int idAlumno, int idCronograma) {
        // ✅ CORREGIDO: Buscar por cronograma en lugar de curso
        // Nota: Necesitaremos agregar este método en InscripcionRepository
        Inscripcion inscripcion = inscripcionRepository
            .findByAlumno_IdAlumnoAndCronograma_IdCronograma(idAlumno, idCronograma)
            .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        inscripcion.setEstadoInscripcion("Cancelado");
        inscripcionRepository.save(inscripcion); // ✅ Cambiar estado en lugar de eliminar
    }
}
