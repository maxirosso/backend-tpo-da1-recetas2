package com.example.demo.datos;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modelo.Alumnos;
import com.example.demo.modelo.AsistenciaCursos;
import com.example.demo.modelo.CronogramaCursos;

public interface AlumnosRepository extends JpaRepository<Alumnos, Integer> {
	List<Alumnos> findByCronogramaCursos(CronogramaCursos cronogramaCursos);
}
