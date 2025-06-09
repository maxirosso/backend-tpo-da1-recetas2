package com.example.demo.datos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.modelo.RecetasGuardadas;
import com.example.demo.modelo.RecetasGuardadasId;

public interface RecetasGuardadasRepository extends JpaRepository<RecetasGuardadas, RecetasGuardadasId> {
    
    // Buscar por usuario
    List<RecetasGuardadas> findByIdUsuario(Integer idUsuario);
    
    // Buscar por receta y usuario específicos
    Optional<RecetasGuardadas> findByIdRecetaAndIdUsuario(Integer idReceta, Integer idUsuario);
    
    // Query para obtener las recetas guardadas con información completa
    @Query("SELECT r FROM RecetasGuardadas r " +
           "JOIN FETCH r.receta " +
           "WHERE r.idUsuario = :idUsuario " +
           "ORDER BY r.fechaGuardada DESC")
    List<RecetasGuardadas> findByIdUsuarioWithRecetaDetails(@Param("idUsuario") Integer idUsuario);
    
    // Eliminar una receta específica de un usuario
    void deleteByIdRecetaAndIdUsuario(Integer idReceta, Integer idUsuario);
    
    // Verificar si existe una receta guardada para un usuario
    boolean existsByIdRecetaAndIdUsuario(Integer idReceta, Integer idUsuario);
} 