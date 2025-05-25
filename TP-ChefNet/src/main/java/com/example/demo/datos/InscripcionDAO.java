package com.example.demo.datos;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Alumnos;
import com.example.demo.modelo.Cursos;
import com.example.demo.modelo.Inscripcion;

@Repository
public class InscripcionDAO {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private AlumnosRepository alumnosRepository;

    @Autowired
    private CursosRepository cursosRepository;

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

    // Inscribir un alumno a un curso
    public void inscribirAlumno(int idAlumno, int idCurso) {
        Alumnos alumno = alumnosRepository.findById(idAlumno)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        Cursos curso = cursosRepository.findById(idCurso)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setAlumno(alumno);
        inscripcion.setCurso(curso);
        inscripcion.setFechaInscripcion(new java.util.Date());
        inscripcion.setEstadoInscripcion("Inscrito"); //puede ser cancelado, etc
        inscripcion.setEstadoPago("Pagado"); //puede ser en proceso, etc
 
        inscripcionRepository.save(inscripcion);
    }

    public void cancelarInscripcion(int idAlumno, int idCurso) {
        Inscripcion inscripcion = inscripcionRepository
            .findByAlumno_IdAlumnoAndCurso_IdCurso(idAlumno, idCurso)
            .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        inscripcionRepository.delete(inscripcion);
        inscripcion.setEstadoInscripcion("Cancelado");
    }
}
