package com.example.demo.datos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modelo.Inscripcion;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    Optional<Inscripcion> findByAlumno_IdAlumnoAndCurso_IdCurso(int idAlumno, int idCurso);
    List<Inscripcion> findByAlumno_IdAlumno(int idAlumno);
    List<Inscripcion> findByIdAlumnoAndEstadoInscripcion(int idAlumno, String estadoInscripcion);


}
