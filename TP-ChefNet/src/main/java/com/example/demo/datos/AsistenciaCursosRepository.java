package com.example.demo.datos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modelo.AsistenciaCursos;

public interface AsistenciaCursosRepository extends JpaRepository<AsistenciaCursos, Integer> {
	Optional<AsistenciaCursos> findByIdAlumno_IdAlumnoAndIdCronograma_IdCronograma(int idAlumno, int idCronograma);


}
