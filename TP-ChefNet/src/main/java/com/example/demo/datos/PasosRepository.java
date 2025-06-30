package com.example.demo.datos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modelo.Pasos;
import com.example.demo.modelo.Recetas;

public interface PasosRepository extends JpaRepository<Pasos, Integer>{
    
    List<Pasos> findByIdRecetaOrderByNroPaso(Recetas receta);
    
    void deleteByIdReceta(Recetas receta);

}
