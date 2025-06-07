package com.example.demo.datos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.modelo.RecetasAIntentar;
import com.example.demo.modelo.RecetasAIntentarId;

public interface RecetasAIntentarRepository extends JpaRepository<RecetasAIntentar, RecetasAIntentarId> {
    
    // Buscar por usuario
    List<RecetasAIntentar> findByIdUsuario(Integer idUsuario);
    
    // Buscar por receta y usuario específicos
    Optional<RecetasAIntentar> findByIdRecetaAndIdUsuario(Integer idReceta, Integer idUsuario);
    
    // Buscar recetas completadas por usuario
    List<RecetasAIntentar> findByIdUsuarioAndCompletadaTrue(Integer idUsuario);
    
    // Buscar recetas pendientes por usuario
    List<RecetasAIntentar> findByIdUsuarioAndCompletadaFalse(Integer idUsuario);
    
    // Query para obtener las recetas con información completa
    @Query("SELECT r FROM RecetasAIntentar r " +
           "JOIN FETCH r.receta " +
           "WHERE r.idUsuario = :idUsuario " +
           "ORDER BY r.fechaAgregada DESC")
    List<RecetasAIntentar> findByIdUsuarioWithRecetaDetails(@Param("idUsuario") Integer idUsuario);
    
    // Eliminar una receta específica de un usuario
    void deleteByIdRecetaAndIdUsuario(Integer idReceta, Integer idUsuario);
    
    // Verificar si existe una receta para un usuario
    boolean existsByIdRecetaAndIdUsuario(Integer idReceta, Integer idUsuario);
} 