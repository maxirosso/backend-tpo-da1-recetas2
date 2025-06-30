package com.example.demo.datos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modelo.Multimedia;
import com.example.demo.modelo.Recetas;

public interface MultimediaRepository extends JpaRepository<Multimedia, Integer>{
    
    List<Multimedia> findByReceta(Recetas receta);
    
    void deleteByReceta(Recetas receta);

}
