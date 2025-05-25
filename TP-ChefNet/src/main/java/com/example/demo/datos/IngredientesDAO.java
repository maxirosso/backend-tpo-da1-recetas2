package com.example.demo.datos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Cursos;
import com.example.demo.modelo.Ingredientes;

@Repository
public class IngredientesDAO {
	@Autowired
	IngredientesRepository ingredientesRepository;
	
	public List<Ingredientes> getAllIngredientes(IngredientesRepository ingredientesRepository){
		return ingredientesRepository.findAll();
	}
	
    public void save(Ingredientes ingredientes) {
    	ingredientesRepository.save(ingredientes);
    }
	
	public void delete(Ingredientes ingredientes) {
		ingredientesRepository.delete(ingredientes);;
	}
	

}
