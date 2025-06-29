package com.example.demo.datos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modelo.Inscripcion;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    // ✅ CORREGIDO: Buscar por cronograma en lugar de curso
    Optional<Inscripcion> findByAlumno_IdAlumnoAndCronograma_IdCronograma(int idAlumno, int idCronograma);
    List<Inscripcion> findByAlumno_IdAlumno(int idAlumno);
    List<Inscripcion> findByIdAlumnoAndEstadoInscripcion(int idAlumno, String estadoInscripcion);
}
