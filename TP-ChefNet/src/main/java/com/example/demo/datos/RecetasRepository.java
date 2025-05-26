package com.example.demo.datos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.modelo.Recetas;
import com.example.demo.modelo.TiposReceta;
import com.example.demo.modelo.Usuarios;

public interface RecetasRepository extends JpaRepository<Recetas, Integer> {
    Optional<Recetas> findById(Integer idReceta);
    List<Recetas> findByIdTipo(TiposReceta idTipo);
    List<Recetas> findByNombreReceta(String nombreReceta);
    List<Recetas> findByUsuario(Usuarios usuario);
    List<Recetas> findByNombreRecetaOrderByNombreReceta(String nombreReceta);
    List<Recetas> findByNombreRecetaOrderByFechaDesc(String nombreReceta);
    List<Recetas> findByIdTipoOrderByIdTipo(TiposReceta idTipo);
    List<Recetas> findByIdTipoOrderByFechaDesc(TiposReceta idTipo);
    List<Recetas> findByUsuarioOrderByUsuario(Usuarios usuario);
    public List<Recetas> findByUsuarioOrderByFechaDesc(Usuarios usuario);
    
    @Query("SELECT DISTINCT r FROM Recetas r " +
    	       "JOIN Utilizados u ON u.receta.id = r.idReceta " +
    	       "JOIN Ingredientes i ON i.idIngrediente = u.ingrediente.id " +
    	       "WHERE i.nombre IN :ingredientes")
    List<Recetas> findByIngredientesNombre(@Param("ingredientes") List<String> ingredientes);
    
    @Query("SELECT r FROM Recetas r " +
    	       "JOIN Utilizados u ON u.receta.id = r.idReceta " +
    	       "JOIN Ingredientes i ON i.idIngrediente = u.ingrediente.id " +
    	       "WHERE i.nombre = :ingrediente " +
    	       "ORDER BY i.nombre")
    	List<Recetas> findByIngredientesOrderByIngredientes(@Param("ingrediente") String ingrediente);
    
    @Query("SELECT r FROM Recetas r " +
    	       "JOIN Utilizados u ON u.receta.id = r.idReceta " +
    	       "JOIN Ingredientes i ON i.idIngrediente = u.ingrediente.id " +
    	       "WHERE i.nombre = :ingrediente " +
    	       "ORDER BY r.fecha DESC")
    List<Recetas> findByIngredientesOrderByFechaDesc(String ingrediente);
    
    @Query("SELECT r FROM Recetas r " +
    	       "WHERE NOT EXISTS (" +
    	       "    SELECT 1 FROM Utilizados u " +
    	       "    JOIN u.ingrediente i " +
    	       "    WHERE u.receta.idReceta = r.idReceta " +
    	       "    AND i.nombre IN :ingredientesAusentes" +
    	       ") " +
    	       "ORDER BY r.nombreReceta")
    List<Recetas> findBySinIngredientes(@Param("ingredientesAusentes") List<String> ingredientesAusentes);

    
    @Query("SELECT r FROM Recetas r WHERE r.idReceta NOT IN (" +
    	       "SELECT u.receta.idReceta FROM Utilizados u " +
    	       "JOIN Ingredientes i ON i.idIngrediente = u.ingrediente.id " +
    	       "WHERE i.nombre = :ingrediente) " +
    	       "ORDER BY r.nombreReceta")
    List<Recetas> findBySinIngredientesOrderByIngredientes(@Param("ingrediente") String ingrediente);

    
    @Query("SELECT r FROM Recetas r WHERE r.idReceta NOT IN (" +
    	       "SELECT u.receta.idReceta FROM Utilizados u " +
    	       "JOIN Ingredientes i ON i.idIngrediente = u.ingrediente.id " +
    	       "WHERE i.nombre = :ingrediente) " +
    	       "ORDER BY r.fecha DESC")
    	List<Recetas> findBySinIngredientesOrderByFechaDesc(@Param("ingrediente") String ingrediente);


    Optional<Recetas> findByNombreRecetaAndUsuario(String nombreReceta, Usuarios usuario);

    // Get the 3 most recent recipes
    List<Recetas> findTop3ByOrderByFechaDesc();

}
