package com.example.demo.datos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modelo.Calificaciones;
import com.example.demo.modelo.Recetas;

public interface CalificacionesRepository extends JpaRepository<Calificaciones, Integer>{
    List<Calificaciones> findByIdReceta(Recetas receta);
    List<Calificaciones> findByIdRecetaAndAutorizadoTrue(Recetas receta);

}
