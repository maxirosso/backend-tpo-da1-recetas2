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
    
    // Partial name search methods (for task compliance)
    List<Recetas> findByNombreRecetaContainingIgnoreCase(String nombreReceta);
    List<Recetas> findByNombreRecetaContainingIgnoreCaseOrderByNombreReceta(String nombreReceta);
    List<Recetas> findByNombreRecetaContainingIgnoreCaseOrderByFechaDesc(String nombreReceta);
    
    // Keep exact match methods for legacy compatibility
    List<Recetas> findByNombreRecetaOrderByNombreReceta(String nombreReceta);
    List<Recetas> findByNombreRecetaOrderByFechaDesc(String nombreReceta);
    
    // User sorting for partial name search
    @Query("SELECT r FROM Recetas r WHERE LOWER(r.nombreReceta) LIKE LOWER(CONCAT('%', :nombreReceta, '%')) ORDER BY r.usuario.nombre")
    List<Recetas> findByNombreRecetaContainingIgnoreCaseOrderByUsuario(@Param("nombreReceta") String nombreReceta);
    List<Recetas> findByIdTipoOrderByIdTipo(TiposReceta idTipo);
    List<Recetas> findByIdTipoOrderByFechaDesc(TiposReceta idTipo);
    List<Recetas> findByUsuarioOrderByUsuario(Usuarios usuario);
    public List<Recetas> findByUsuarioOrderByFechaDesc(Usuarios usuario);
    
    // Search recipes by partial user name (for task compliance)
    @Query("SELECT r FROM Recetas r JOIN r.usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :userName, '%')) ORDER BY r.nombreReceta")
    List<Recetas> findByUsuarioNombreContainingIgnoreCaseOrderByNombreReceta(@Param("userName") String userName);
    
    @Query("SELECT r FROM Recetas r JOIN r.usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :userName, '%')) ORDER BY r.fecha DESC")
    List<Recetas> findByUsuarioNombreContainingIgnoreCaseOrderByFechaDesc(@Param("userName") String userName);
    
    @Query("SELECT r FROM Recetas r JOIN r.usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :userName, '%')) ORDER BY u.nombre")
    List<Recetas> findByUsuarioNombreContainingIgnoreCaseOrderByUsuario(@Param("userName") String userName);
    
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
    	       "JOIN Utilizados u ON u.receta.id = r.idReceta " +
    	       "JOIN Ingredientes i ON i.idIngrediente = u.ingrediente.id " +
    	       "WHERE i.nombre = :ingrediente " +
    	       "ORDER BY r.usuario.nombre")
    List<Recetas> findByIngredientesOrderByUsuario(String ingrediente);
    
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

    @Query("SELECT r FROM Recetas r WHERE r.idReceta NOT IN (" +
    	       "SELECT u.receta.idReceta FROM Utilizados u " +
    	       "JOIN Ingredientes i ON i.idIngrediente = u.ingrediente.id " +
    	       "WHERE i.nombre = :ingrediente) " +
    	       "ORDER BY r.usuario.nombre")
    	List<Recetas> findBySinIngredientesOrderByUsuario(@Param("ingrediente") String ingrediente);


    Optional<Recetas> findByNombreRecetaAndUsuario(String nombreReceta, Usuarios usuario);

    // Get the 3 most recent recipes
    List<Recetas> findTop3ByOrderByFechaDesc();

}
